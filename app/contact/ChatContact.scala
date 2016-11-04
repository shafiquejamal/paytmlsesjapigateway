package contact

import java.util.UUID

sealed trait ChatContactVisibility { def number: Int }

object ChatContactVisibility {

  case object Visible extends ChatContactVisibility { override val number = 1 }
  case object NotVisible extends ChatContactVisibility { override val number = 0 }

  private val all = Seq(Visible, NotVisible)
  def from(number: Int) = all.find(_.number == number).getOrElse(NotVisible)

}

case class ChatContact(contactUserId: UUID, contactUsername: String)

case class ChatContactWithVisibility(contact: ChatContact, contactVisibility: ChatContactVisibility)
