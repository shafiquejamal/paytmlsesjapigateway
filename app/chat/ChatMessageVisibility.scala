package chat

sealed trait ChatMessageVisibility { def number: Int }

object ChatMessageVisibility {

  case object Both extends ChatMessageVisibility { override val number = 3 }
  case object SenderOnly extends ChatMessageVisibility { override val number = 1 }
  case object ReceiverOnly extends ChatMessageVisibility { override val number = 2 }
  case object Neither extends ChatMessageVisibility { override val number = 0 }

  val all = Seq(Both, SenderOnly, ReceiverOnly, Neither)
  def from(number: Int) = all.find(_.number == number).getOrElse(Neither)

}