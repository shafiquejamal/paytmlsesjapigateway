package plumbing

import akka.actor.{Actor, ActorLogging, Props}
import play.api.libs.json.JsValue

class MessageBrokerMessageDispatcher(publish: (JsValue, String) => Unit) extends Actor with ActorLogging {

  override def receive = {

    case message =>
      log.info("Some other message received", message)

  }

}

object MessageBrokerMessageDispatcher {

  def props(publish: (JsValue, String) => Unit) = Props(new MessageBrokerMessageDispatcher(publish))

}