lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.sasdutta",
      scalaVersion := "2.12.12"
    )),
    name := "spark-typed-data-poc",
    version := "0.0.1"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.0.1",
  "io.getquill" %% "quill-spark" % "3.6.0-RC3",
  "org.typelevel" %% "frameless-dataset" % "0.9.0"
)

javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled")
