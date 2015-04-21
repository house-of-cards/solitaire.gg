package utils.parser

import models.game.rules._

object ScalaPyramidExporter {
  def exportPyramids(rules: GameRules, ret: StringBuilder) = {
    def add(s: String) = ret ++= s + "\n"
    def cls(o: Any) = ScalaExporter.cls(o)

    if(rules.pyramids.isEmpty) {
      add( s"""  pyramids = Nil""")
    } else {
      add(s"""  pyramids = Seq(""")
      add(rules.pyramids.map { p =>
        s"""    PyramidSet(\n""" +
        s"""      name = "${p.name.replaceAllLiterally("\"", "")}",\n""" +
        s"""      pyramidType = PyramidType.${p.pyramidType},\n""" +
        s"""      height = ${p.height},\n""" +
        s"""      cardsFaceDown = PyramidFaceDownCards.${cls(p.cardsFaceDown)},\n""" +
        s"""      suitMatchRuleForBuilding = SuitMatchRule.${cls(p.suitMatchRuleForBuilding)},\n""" +
        s"""      rankMatchRuleForBuilding = RankMatchRule.${cls(p.rankMatchRuleForBuilding)},\n""" +
        s"""      wrapFromKingToAce = ${p.wrapFromKingToAce},\n""" +
        s"""      suitMatchRuleForMovingStacks = SuitMatchRule.${cls(p.suitMatchRuleForMovingStacks)},\n""" +
        s"""      rankMatchRuleForMovingStacks = RankMatchRule.${cls(p.rankMatchRuleForMovingStacks)},\n""" +
        s"""      mayMoveToNonEmptyFrom = Seq(${p.mayMoveToNonEmptyFrom.map(x => "\"" + x + "\"").mkString(", ")}),\n""" +
        s"""      mayMoveToEmptyFrom = Seq(${p.mayMoveToEmptyFrom.map(x => "\"" + x + "\"").mkString(", ")}),\n""" +
        s"""      emptyFilledWith = PyramidFillEmptyWith.${cls(p.emptyFilledWith)}\n""" +
        s"""    )"""
      }.mkString(",\n"))
      add(s"""  )""")
    }
  }
}