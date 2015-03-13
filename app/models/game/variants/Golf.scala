package models.game.variants

import models.game._
import models.game.pile.constraints.{DragToConstraints, DragFromConstraints}
import models.game.pile.{PileOptions, Stock, Foundation, Tableau}

object Golf extends GameVariant.Description {
  override val id = "golf"
  override val name = "Golf"
  override val body = "Build the bottom pile up or down regardless of suit. Ranking of cards is not continuous: an Ace may be built only on a 2, a King only on a Queen."
}

case class Golf(override val id: String, override val seed: Int) extends GameVariant(id, seed) {
  override def description = Golf

  private val tableauOptions = PileOptions(dragFromConstraint = Some(DragFromConstraints.topCardOnly), dragToConstraint = None)

  private val piles = List(
    new Tableau("tableau-1", tableauOptions),
    new Tableau("tableau-2", tableauOptions),
    new Tableau("tableau-3", tableauOptions),
    new Tableau("tableau-4", tableauOptions),
    new Tableau("tableau-5", tableauOptions),
    new Tableau("tableau-6", tableauOptions),
    new Tableau("tableau-7", tableauOptions),

    new Foundation("foundation", PileOptions(cardsShown = Some(4), direction = Some("r"), dragToConstraint = Some(DragToConstraints.alternatingRank))),
    new Stock("stock", 1, "foundation", None, PileOptions(cardsShown = Some(16), direction = Some("r")))
  )

  private val deck = Deck.shuffled(rng)

  private val layouts = Seq(
    Layout(
      width = 7.8,
      height = 3.1,
      piles = List(
        PileLocation("tableau-1", 0.1, 0.2),
        PileLocation("tableau-2", 1.2, 0.2),
        PileLocation("tableau-3", 2.3, 0.2),
        PileLocation("tableau-4", 3.4, 0.2),
        PileLocation("tableau-5", 4.5, 0.2),
        PileLocation("tableau-6", 5.6, 0.2),
        PileLocation("tableau-7", 6.7, 0.2),
        PileLocation("foundation", 0.1, 2.0),
        PileLocation("stock", 2.2, 2.0)
      )
    )
  )

  val gameState = GameState(id, description.id, seed, deck, piles, layouts)

  override def initialMoves() = {
    gameState.addCards(deck.getCards(5, turnFaceUp = true), "tableau-1", reveal = true)
    gameState.addCards(deck.getCards(5, turnFaceUp = true), "tableau-2", reveal = true)
    gameState.addCards(deck.getCards(5, turnFaceUp = true), "tableau-3", reveal = true)
    gameState.addCards(deck.getCards(5, turnFaceUp = true), "tableau-4", reveal = true)
    gameState.addCards(deck.getCards(5, turnFaceUp = true), "tableau-5", reveal = true)
    gameState.addCards(deck.getCards(5, turnFaceUp = true), "tableau-6", reveal = true)
    gameState.addCards(deck.getCards(5, turnFaceUp = true), "tableau-7", reveal = true)

    gameState.addCards(deck.getCards(1, turnFaceUp = true), "foundation", reveal = true)
    gameState.addCards(deck.getCards(), "stock")
  }

  override def isWin: Boolean = gameState.pilesById("foundation").cards.length == 52
}
