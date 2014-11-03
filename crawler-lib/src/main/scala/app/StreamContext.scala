package org.blikk.crawler.app

import akka.actor._
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Source
import com.rabbitmq.client.{Channel => RabbitChannel}
import org.blikk.crawler.ImplicitLogging

/** 
  * The context for a running streaming application.
  * Within a running application, you can interact with the API client `api`
  * The flow of the streaming context can only be consumed once.
  */
case class StreamContext[A](appId: String, flow: Source[A], publisher: ActorRef)
  (implicit _system: ActorSystem, _rabbitChannel: RabbitChannel, _materializer: FlowMaterializer) 
  extends ImplicitLogging {

  implicit val materializer = _materializer
  implicit val system = _system
  implicit val rabbitChannel = _rabbitChannel

  def shutdown(){
    system.synchronized {
      log.info("shutting down app={}", appId)
      _system.stop(publisher)
    }
  }

}