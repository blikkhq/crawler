package org.blikk.crawler.processors

import akka.stream.scaladsl2._
import org.blikk.crawler._
import org.blikk.crawler.app._

object TerminationSink {

  /* Builds a sink that defines termination conditions based on crawl statistics */
  def build(f: CrawlStats => Boolean)(implicit ctx: StreamContext[_]) = {
    
    import ctx.{log, system}

    val zeroStats = CrawlStats(0, 0, System.currentTimeMillis)

    FoldSink[CrawlStats, CrawlItem](zeroStats) { (currentStats, item) =>
      val newStats = currentStats.update(item)
      if(f(newStats)) {
        // Shutdown if the termination condition is fulfilled
        log.info("Terminating with: {}", newStats)
        ctx.publisher !  RabbitPublisher.CompleteStream
        system.stop(ctx.publisher)
      }
      newStats
    }
  }

}