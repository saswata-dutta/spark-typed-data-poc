import java.io.File

import org.apache.spark.sql.SparkSession

import scala.language.postfixOps
import scala.sys.process._


object Main {
  def main(args: Array[String]): Unit = {
    withSpark { spark =>
      import spark.implicits._
      val tweets = List(Tweet("some #hashTAG #h2"), Tweet("dds #h2 #hashtag #h2 #h3")).toDS()

      println("=== QUILL TWITTER ===")
      new QuillTopHashtag(spark).topHashtags(tweets, 10).show()

      val files = fileNames()
      downloadGithubArchives(files)

      println("=== QUILL GITHUB ===")
      new QuillGithubActivities(spark).process(files)

      println("=== FRAMELESS GITHUB ===")
      new FramelessGithubActivities(spark).process(files)
    }
  }

  def initSpark(): SparkSession = {
    disableSparkLogs()

    val spark = SparkSession
      .builder()
      .master("local")
      .appName("spark session")
      .config("spark.sql.shuffle.partitions", "1") // for local demo
      .config("spark.ui.enabled", "false")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    spark
  }

  def disableSparkLogs(): Unit = {
    import org.apache.log4j.{Level, Logger}

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
  }

  def withSpark(func: SparkSession => Unit): Unit = {
    val spark = initSpark()
    spark.sparkContext.setCheckpointDir(System.getProperty("java.io.tmpdir"))

    try {
      func(spark)
    } finally {
      spark.stop()
      System.clearProperty("spark.driver.port")
    }
  }

  def fileNames(): Seq[String] = for {
    year <- 2011 to 2011
    month <- 2 to 2
    day <- 12 to 12
    hour <- 11 to 11
  } yield "%04d-%02d-%02d-%d".format(year, month, day, hour) + ".json.gz"

  def downloadGithubArchives(files: Seq[String]): Unit = {
    files.par.foreach { name =>
      val file = new File(name)
      if (!file.exists()) {
        println(s"downloading missing file $name")
        //        new URL(s"http://data.githubarchive.org/$name") #> new File(name) !!
        s"wget http://data.githubarchive.org/$name" !!
      }
    }
  }
}
