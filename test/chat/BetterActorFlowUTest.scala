package chat

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, ShouldMatchers}
import util.TestTimeProviderImpl
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration.FiniteDuration

class BetterActorFlowUTest(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with FlatSpecLike
  with ShouldMatchers
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("MySpec"))
  val timeProvider = new TestTimeProviderImpl

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  implicit val materializer = ActorMaterializer()

  "Creating an actor flow with a name" should "do something" in {

    BetterActorFlow.namedActorRef(
      client => ChatActor.props(client, null, new UUID(1, 1), "someClient", timeProvider),
      16,
      OverflowStrategy.dropNew,
      "my_actor")

    val selection = system.actorSelection("user/my_*")

    implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))
    selection.resolveOne().onComplete {
      case Success(actorRef) =>
        actorRef.path.name shouldBe "my_actor"
      case Failure(ex) =>
        ex should not be an[Exception]
    }


  }

}
