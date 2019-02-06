package models

sealed trait FRENavigatorOptions

object FRENavigatorOptions {

  case object NoFRE extends FRENavigatorOptions
  case object AllYearsSameAmounts extends FRENavigatorOptions
  case object AllYearsDifferentAmounts extends FRENavigatorOptions
  case object SomeYears extends FRENavigatorOptions

}