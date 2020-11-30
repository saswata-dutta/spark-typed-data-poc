import io.getquill.Ord
import io.getquill.QuillSparkContext._
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

class QuillTopHashtag(spark: SparkSession) {
  implicit val sqlContext: SQLContext = spark.sqlContext
  import sqlContext.implicits._

  def topHashtags(tweets: Dataset[Tweet], n: Int): Dataset[HashTagCount] =
    run { // produce a dataset from the Quill query
      liftQuery(tweets) // transform the dataset into a Quill query
        .concatMap(_.text.split(" ")) // split into words and unnest results
        .filter(_.startsWith("#")) // filter hashtag words
        .map(_.toLowerCase) // normalize hashtags
        .groupBy(it => it) // group by each hashtag
        .map { // map tag list to its count
          case (tag, list) =>
            HashTagCount(tag, list.size)
        }
        .sortBy(_.count)(Ord.desc)
        .take(lift(n)) // limit to top results
    }
}
