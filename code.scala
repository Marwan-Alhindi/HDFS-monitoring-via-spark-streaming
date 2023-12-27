package stream_processor
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamProcessor {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: StreamProcessor <input-dir> <output-dir>")
      System.exit(1)
    }

    val sourceDir = args(0)
    val targetDir = args(1)

    val conf = new SparkConf().setAppName("StreamProcessor").setMaster("local[*]")
    val context = new StreamingContext(conf, Seconds(3))

    context.checkpoint(".") // Setting checkpoint directory to the current directory

    val fileStream = context.textFileStream(sourceDir)

    def isAlphaWord(word: String): Boolean = word.matches("[a-zA-Z]+") && word.length >= 3

    val alphaWords = fileStream.flatMap(_.split("\\s+")).filter(isAlphaWord)

    // Task A
    var taskACount = 1
    alphaWords.map((_, 1)).reduceByKey(_ + _).foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
        rdd.saveAsTextFile(s"$targetDir/taskA-${taskACount.formatted("%03d")}")
        taskACount += 1
      }
    }

    // Task B
    var taskBCount = 1
    fileStream.flatMap { line =>
      val words = line.split("\\s+").filter(isAlphaWord)
      for {
        a <- words.indices
        b <- words.indices if a != b
      } yield (s"${words(a)} ${words(b)}", 1)
    }.reduceByKey(_ + _).foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
        rdd.saveAsTextFile(s"$targetDir/taskB-${taskBCount.formatted("%03d")}")
        taskBCount += 1
      }
    }

    // Task C
    var taskCCount = 1
    val stateUpdateFunc = (pairs: Seq[Int], state: Option[Int]) => {
      Some(pairs.sum + state.getOrElse(0))
    }

    fileStream.flatMap { line =>
      val words = line.split("\\s+").filter(isAlphaWord)
      for {
        i <- words.indices
        j <- words.indices if i != j
      } yield (s"${words(i)} ${words(j)}", 1)
    }.updateStateByKey(stateUpdateFunc).foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
        rdd.saveAsTextFile(s"$targetDir/taskC-${taskCCount.formatted("%03d")}")
        taskCCount += 1
      }
    }

    context.start()
    context.awaitTermination()
  }
}