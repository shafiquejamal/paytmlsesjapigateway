package contact

import java.util.UUID

sealed trait ContactVisibility { def number: Int }

object ContactVisibility {

  case object Visible extends ContactVisibility { override val number = 1 }
  case object NotVisible extends ContactVisibility { override val number = 0 }

  val all = Seq(Visible, NotVisible)
  def from(number: Int) = all.find(_.number == number).getOrElse(NotVisible)

}


case class Contact(contactUserId: UUID, contactUsername: String)

case class ContactWithVisibility(contact: Contact, contactVisibility: ContactVisibility)
