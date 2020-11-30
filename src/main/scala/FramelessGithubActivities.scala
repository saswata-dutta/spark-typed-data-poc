import frameless.TypedDataset
import frameless.functions.aggregate.count
import org.apache.spark.sql.SparkSession


class FramelessGithubActivities(spark: SparkSession) {

  import frameless.syntax._
  import spark.implicits._

  def process(files: Seq[String]): Unit = {
    val data = spark.read.json(files: _*).
      select("id", "type", "actor", "repo", "created_at", "org")

    val activities =
      TypedDataset.create(data.as[Activity])

    activities.select(activities.colMany('actor, 'login),
      activities.colMany('repo, 'name)).show(5).run()

    val starEvents = activities.filter(activities('type) === "WatchEvent")

    val actors = starEvents.select(starEvents('actor))
    val login = actors('login)
    val actorCounts = actors.groupBy(login).agg(count(login))
    val topStargazers = actorCounts.orderBy(actorCounts('_2).desc)

    val repos = starEvents.select(starEvents('repo))
    val repoName = repos('name)
    val repoCounts = repos.groupBy(repoName).agg(count())
    val topRepos = repoCounts.orderBy(repoCounts('_2).desc)

    topStargazers.show().run()
    topRepos.show().run()
  }
}
