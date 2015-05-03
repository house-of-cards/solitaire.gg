package models.game.pile.constraints

import models.game.pile.Pile
import models.game.{ GameState, Rank, Card }

object KlondikeConstraintLogic {
  val dragFrom = (pile: Pile, cards: Seq[Card], gameState: GameState) => if (cards.exists(!_.u)) {
    false
  } else {
    var valid = true
    var lastCard: Option[Card] = None

    for (c <- cards) {
      lastCard.foreach { lc =>
        if (c.s.color == lc.s.color) { valid = false }
        if (c.r == Rank.Ace || c.r.value != (lc.r.value - 1)) { valid = false }
      }
      lastCard = Some(c)
    }
    valid
  }

  def tableauDragTo(emptyPileRank: Option[Rank]) = (pile: Pile, cards: Seq[Card], gameState: GameState) => if (pile.cards.isEmpty) {
    emptyPileRank match {
      case Some(r) => cards.headOption.getOrElse(throw new IllegalStateException()).r == r
      case None => cards.length == 1
    }
  } else {
    val topCard = pile.cards.lastOption.getOrElse(throw new IllegalStateException())
    val firstDraggedCard = cards.headOption.getOrElse(throw new IllegalStateException())
    if (!topCard.u) {
      false
    } else {
      if (topCard.s.color == firstDraggedCard.s.color) {
        false
      } else if (topCard.r == Rank.Ace || firstDraggedCard.r == Rank.King) {
        false
      } else {
        topCard.r.value == firstDraggedCard.r.value + 1
      }
    }
  }

  val selectCard = (pile: Pile, cards: Seq[Card], gameState: GameState) => {
    val firstCard = cards.headOption.getOrElse(throw new IllegalStateException())
    if (pile.cards.lastOption != cards.headOption) {
      false
    } else if (!firstCard.u) {
      true
    } else {
      val foundations = gameState.pileSets.filter(_.behavior == "foundation").flatMap(_.piles)
      val foundation = foundations.flatMap { f =>
        if (f.cards.isEmpty && firstCard.r == Rank.Ace) {
          Some(f)
        } else if (f.cards.lastOption.map(_.s) == Some(firstCard.s) && f.cards.lastOption.map(_.r) == Some(Rank.Ace) && firstCard.r == Rank.Two) {
          Some(f)
        } else if (f.cards.lastOption.map(_.s) == Some(firstCard.s) && f.cards.lastOption.map(_.r.value) == Some(firstCard.r.value - 1)) {
          Some(f)
        } else {
          None
        }
      }.headOption
      foundation match {
        case Some(f) => true
        case None => false
      }
    }
  }
}
