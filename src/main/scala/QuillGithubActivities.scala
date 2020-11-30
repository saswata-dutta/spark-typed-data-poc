import io.getquill.QuillSparkContext._
import io.getquill.{Ord, Query}
import org.apache.spark.sql.{SQLContext, SparkSession}


class QuillGithubActivities(spark: SparkSession) {
  implicit val sqlContext: SQLContext = spark.sqlContext

  import sqlContext.implicits._

  def process(files: Seq[String]): Unit = {

    val activities =
      liftQuery(sqlContext.read.json(files: _*).as[Activity])

    run(quote(activities.map(it => (it.actor.login, it.repo.name)))).show(5)

    val starEvents =
      quote(activities.filter(_.`type` == "WatchEvent"))

    val topStargazers = quote {
      starEvents
        .groupBy(_.actor)
        .map {
          case (actor, list) => (actor.login, list.size)
        }.sortBy {
        case (_, size) => size
      }(Ord.desc)
    }

    val topProjects = quote {
      starEvents
        .groupBy(_.repo)
        .map {
          case (repo, list) => (repo.name, list.size)
        }.sortBy {
        case (_, size) => size
      }(Ord.desc)
    }

    run(topStargazers).show()
    run(topProjects).show()
  }
}
