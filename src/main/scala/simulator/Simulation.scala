package simulator

import cats.syntax.either._
import io.circe._
import io.circe.generic.auto._
import io.circe.yaml

import scala.io.Source._
import scala.math._
import scala.annotation.tailrec

case class Loan(amount: Double, interest: Double)
case class Contribution(amount: Int, period: Int)
case class Configuration(loan: Loan, simulations: List[Contribution])
case class Result(contribution: Contribution, days: Int)

object simulate {
  @tailrec
  def apply(loan: Loan, contribution: Contribution, elapsed: Int = 0): Result = loan match {
    case Loan(0, _) => Result(contribution, elapsed)
    case Loan(Double.PositiveInfinity, _) => Result(contribution, -1)
    case Loan(amount, interest) => {
      val newAmount = amount + amount * interest
      val loan = if (elapsed > 0 && elapsed % contribution.period == 0) Loan(max(newAmount - contribution.amount, 0), interest) else Loan(newAmount, interest)
      // println(s"Step $loan")
      simulate(loan, contribution, elapsed + 1)
    }
  }
}

object Simulation extends App {
  if (args.length != 1) {
    println("Usage: simulate <filename>.yaml")
    System.exit(1)
  }

  val file: String = fromFile(args(0)).getLines mkString "\n"
  val json = yaml.parser.parse(file);
  val config = json
    .leftMap(err => err: Error)
    .flatMap(_.as[Configuration])
    .valueOr(throw _)

  val results: List[Result] = config.simulations
    .map(contribution => {
      // println(s"Starting simulation with $contribution")
      simulate(config.loan, contribution, 0)
    })


  results.foreach(_ match {
    case Result(contribution, -1) => println(s"Contributing ${contribution.amount} every ${contribution.period} days will never end your loan")
    case Result(contribution, days) => println(s"Contributing ${contribution.amount} every ${contribution.period} days ends your loan in ${days / 7} weeks")
  })
}
