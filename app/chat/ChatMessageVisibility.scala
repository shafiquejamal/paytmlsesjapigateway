package chat

sealed trait ChatMessageVisibility { def number: Int }

object ChatMessageVisibility {

  case object Visible extends ChatMessageVisibility { override val number = 1 }
  case object NotVisible extends ChatMessageVisibility { override val number = 0 }

  val all = Seq(Visible, NotVisible)
  def from(number: Int) = all.find(_.number == number).getOrElse(NotVisible)

}