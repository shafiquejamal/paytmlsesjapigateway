package contact.phone

import java.util.UUID

import access.registration.ActivationCodeGenerator
import com.google.inject.Inject
import contact.phone.PhoneNumberStatus.Unverified
import play.api.Configuration
import util.TimeProvider

import scala.util.{Failure, Success, Try}

class PhoneFacade @Inject() (
    phoneDAO: PhoneDAO,
    timeProvider: TimeProvider,
    phoneVerificationCodeSender: PhoneVerificationCodeSender,
    configuration: Configuration) extends PhoneAPI {

  override def registerPhoneNumber(forUser: UUID, phoneNumberRegistration: PhoneNumberRegistrationMessage):
  Try[PhoneNumber] = {
    val key = configuration.getString(ActivationCodeGenerator.configurationKey).getOrElse("")
    val phoneNumber = PhoneNumber(phoneNumberRegistration.phoneNumberToAdd, Unverified, timeProvider.now())
    phoneDAO.phoneNumber(forUser).filter(_.phoneNumber.number == phoneNumberRegistration.phoneNumberToAdd).fold {

      val maybePhoneNumber = phoneDAO.addPhoneNumber(forUser, phoneNumber)

      maybePhoneNumber match {
        case Success(phoneNumberId) =>
          sendActivationCode(phoneNumberId, key, phoneNumber)
        case _ =>
          Failure(new Exception("Could not register phone number"))
      }

    } { registeredPhoneNumber => sendActivationCode(registeredPhoneNumber.id, key, registeredPhoneNumber.phoneNumber) }

  }

  private def sendActivationCode(phoneNumberId: UUID, key: String, phoneNumber: PhoneNumber): Try[PhoneNumber] = {
    val activationCode = ActivationCodeGenerator.generateWithDashes(phoneNumberId.toString, key)
      phoneVerificationCodeSender.send(activationCode, phoneNumber)
      Success(phoneNumber)
  }

}
