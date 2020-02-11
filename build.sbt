libraryDependencies += "org.yaml" % "snakeyaml" % "1.25"
libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % "0.12.3",
    "io.circe" %% "circe-generic" % "0.12.3",
    "io.circe" %% "circe-parser" % "0.12.3",
    "io.circe" %% "circe-yaml" % "0.12.0"
)

mainClass in (Compile, run) := Some("simulator.Simulation")
