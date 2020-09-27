package com.alec.mad.assignment1.state

import android.os.SystemClock
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.model.Flag
import com.alec.mad.assignment1.model.Question
import kotlin.random.Random

/**
 * Holds the state of the game; all of the Flags and the player's state. Uses the observer pattern
 * to allow classes to be notified on changes.
 */
class GameState(
    observers: Collection<GameStateObserver>
) : ObservableState<GameStateObserver>(observers) {

    // When no observers are given
    constructor() : this(setOf())

    /**
     * Immutable storage for flags.
     */
    val flags: List<Flag>

    /**
     * The player's mutable point count. Changes are observed to onUpdatePlayerPoints.
     */
    var playerPoints: Int = 0
        set(value) {
            // Get player condition before
            val originalCondition = this.playerCondition

            field = value

            notifyObservers { it.onUpdatePlayerPoints(value) }

            val afterCondition = this.playerCondition
            if (originalCondition != afterCondition) {
                notifyObservers { it.onUpdatePlayerCondition(afterCondition) }
            }
        }

    /**
     * The player's current condition. Computed on access. Changes are observed to
     * onUpdatePlayerCondition.
     */
    val playerCondition: PlayerCondition
        get() = when {
            this.playerPoints <= 0 -> PlayerCondition.LOST
            this.playerPoints >= this.targetPoints -> PlayerCondition.WON
            else -> PlayerCondition.PLAYING
        }

    /**
     * No. of points to reach to win (inclusive).
     */
    val targetPoints: Int

    /* Initialisation handled with a local variable of GameStateInitializer so that all of it's
     * data doesn't stick around in memory after initialisation */
    init {
        val init = GameStateInitializer(Random(SystemClock.currentThreadTimeMillis()))
        this.flags = init.initFlags()
        this.targetPoints = init.initTargetPoints()
        this.playerPoints = init.initPlayerPoints()
        /* Note: targetPoints must be initialised before playerPoints, because ints auto-initialise
         * to 0 on the JVM, and if targetPoints and playerPoints are both zero, the player has
         * technically won before the game starts */
    }

    enum class PlayerCondition {
        /** Player is currently playing the game */
        PLAYING,

        /** Player has reached the target points */
        WON,

        /** Player's points have decreased to zero. */
        LOST
    }

    /**
     * Used to initialise the game state.
     */
    private class GameStateInitializer(rng: Random) {

        val stateGen = GameStateRandomGen(rng)
        val flagHandler = FlagHandler(stateGen)
        val questionHandler = QuestionHandler(stateGen, flagHandler)

        /**
         * Builds a list of flags.
         */
        fun initFlags(): List<Flag> = this.flagHandler.getFlags(this::initFlagQuestions)

        /**
         * Gets a player's starting points.
         */
        fun initPlayerPoints(): Int = this.stateGen.nextStartingPoints

        /**
         * Gets a player's target points.
         */
        fun initTargetPoints(): Int = this.stateGen.nextTargetPoints

        /**
         * Builds a list of questions for a flag of given name.
         */
        fun initFlagQuestions(flagTemplate: FlagTemplate) =
            this.questionHandler.getQuestions(flagTemplate)

        /**
         * Holds the information necessary to create a flag.
         */
        private class FlagTemplate(
            val drawableId: Int,
            val country: String,
            val capital: String,
            val continent: String,
            val language: String
        )

        /**
         * Holds the information necessary to create a question for a flag.
         */
        private class QuestionTemplate(
            val questionText: String,
            val answers: List<String>,
            val correctAnswer: String
        )

        /**
         * Handles random number generation.
         */
        private class GameStateRandomGen(val rng: Random) {

            companion object {
                // Constants for ranges of different randomly generated values
                const val STARTING_POINTS_LOWER = 10
                const val STARTING_POINTS_UPPER = 15
                const val TARGET_POINTS_LOWER = 60
                const val TARGET_POINTS_UPPER = 80
                const val NO_QUESTIONS_LOWER = 5
                const val NO_QUESTIONS_UPPER = 11
                const val QUESTION_POINTS_LOWER = 5
                const val QUESTION_POINTS_UPPER = 16
                const val QUESTION_PENALTY_LOWER = 0
                const val QUESTION_PENALTY_UPPER = 11
                const val QUESTION_SPECIAL_PROB = 5
                const val QUESTION_ANSWERS_LOWER = 2
                const val QUESTION_ANSWERS_UPPER = 4
            }

            /** Get the next value for player starting points */
            val nextStartingPoints
                get() = rng.nextInt(STARTING_POINTS_LOWER, STARTING_POINTS_UPPER)

            val nextTargetPoints
                get() = rng.nextInt(TARGET_POINTS_LOWER, TARGET_POINTS_UPPER)

            /** Get the next value for the number of questions for a flag */
            val nextNoQuestions
                get() = rng.nextInt(NO_QUESTIONS_LOWER, NO_QUESTIONS_UPPER)

            /** Get the next value for the point value of a question */
            val nextQuestionPoints
                get() = rng.nextInt(QUESTION_POINTS_LOWER, QUESTION_POINTS_UPPER)

            /** Get the next value for the penalty value of a question */
            val nextQuestionPenalty
                get() = rng.nextInt(QUESTION_PENALTY_LOWER, QUESTION_PENALTY_UPPER)

            /** Get the next value for the isSpecial of a question */
            val nextQuestionIsSpecial
                get() = rng.nextInt(0, QUESTION_SPECIAL_PROB) == 0

            /** Get the next number of answers for a question to have */
            val nextNumAnswers
                get() = rng.nextInt(QUESTION_ANSWERS_LOWER, QUESTION_ANSWERS_UPPER)
        }

        /**
         * Handles flags.
         */
        private class FlagHandler(val stateGen: GameStateRandomGen) {
            /** List of pairs of name, drawable of each flag */
            val flagTemplates = listOf(
                FlagTemplate(R.drawable.flag_au, "Australia", "Canberra", "Oceania", "English"),
                FlagTemplate(
                    R.drawable.flag_ca, "Canada", "Ottawa", "North America", "English/French"
                ),
                FlagTemplate(R.drawable.flag_bg, "Bulgaria", "Sofia", "Europe", "Bulgarian"),
                FlagTemplate(R.drawable.flag_cz, "The Czech Republic", "Prague", "Europe", "Czech"),
                FlagTemplate(R.drawable.flag_de, "Germany", "Berlin", "Europe", "German"),
                FlagTemplate(R.drawable.flag_es, "Spain", "Madrid", "Europe/Africa", "Spanish"),
                FlagTemplate(R.drawable.flag_fr, "France", "Paris", "Europe", "French"),
                FlagTemplate(
                    R.drawable.flag_hk, "Hong Kong", "City of Victoria", "Asia", "Chinese/English"
                ),
                FlagTemplate(R.drawable.flag_jp, "Japan", "Tokyo", "Asia", "Japanese"),
                FlagTemplate(R.drawable.flag_kr, "South Korea", "Seoul", "Asia", "Korean"),
                FlagTemplate(R.drawable.flag_my, "Malaysia", "Kuala Lumpur", "Asia", "Malay"),
                FlagTemplate(R.drawable.flag_nl, "The Netherlands", "Amsterdam", "Europe", "Dutch"),
                FlagTemplate(R.drawable.flag_pl, "Poland", "Warsaw", "Europe", "Polish"),
                FlagTemplate(R.drawable.flag_ru, "Russia", "Moscow", "Europe/Asia", "Russian"),
                FlagTemplate(
                    R.drawable.flag_uk, "The United Kingdom", "London", "Europe", "English"
                ),
                FlagTemplate(R.drawable.flag_us, "USA", "Washington", "North America", "English")
            ).shuffled(this.stateGen.rng)

            private var flagIterator: Iterator<FlagTemplate> = flagTemplates.iterator()

            /**
             * Gets the next flag template that matches the given condition.
             *
             * This will recurse infinitely if no match is found!
             */
            tailrec fun nextFlagWhere(check: (FlagTemplate) -> Boolean): FlagTemplate {
                if (!flagIterator.hasNext()) {
                    // If the iterator is at it's end, reshuffle the flags and loop back around
                    flagIterator = flagTemplates.shuffled(this.stateGen.rng).iterator()
                }

                val flag = flagIterator.next()

                // If the check passes it returns the flag, otherwise recursion!
                return if (check(flag)) flag else nextFlagWhere(check)
            }

            inline fun getFlags(qnGenerator: (FlagTemplate) -> List<Question>) =
                flagTemplates.map { flagTemplate ->
                    Flag(qnGenerator(flagTemplate), flagTemplate.drawableId)
                }
        }

        /**
         * Handles questions.
         */
        private class QuestionHandler(
            val stateGen: GameStateRandomGen, val flagHandler: FlagHandler
        ) {

            /**
             * Generates a list of questions for the flag of given name.
             */
            fun getQuestions(flagTemplate: FlagTemplate) =
                List(this.stateGen.nextNoQuestions) { idx ->
                    val qnGenerator = questionTemplateGenerators.random(this.stateGen.rng)
                    val qnTemplate = qnGenerator(flagTemplate)
                    Question(
                        idx + 1,
                        this.stateGen.nextQuestionPoints,
                        this.stateGen.nextQuestionPenalty,
                        this.stateGen.nextQuestionIsSpecial,
                        qnTemplate.questionText,
                        qnTemplate.answers,
                        qnTemplate.correctAnswer
                    )
                }

            /**
             * Holds some lambdas that take in a flag's name and generate a question template for
             * that flag.
             */
            val questionTemplateGenerators =
                setOf<(flagTemplate: FlagTemplate) -> QuestionTemplate>(
                    { flagTemplate -> // 'Is this the flag of x'
                        when (this.stateGen.rng.nextBoolean()) {
                            true -> QuestionTemplate(
                                "Is this the flag of ${flagTemplate.country}?",
                                listOf("Yes", "No"),
                                "Yes"
                            )
                            false -> QuestionTemplate(
                                "Is this the flag of ${
                                    this.flagHandler.nextFlagWhere { it != flagTemplate }.country
                                }?",
                                listOf("Yes", "No"),
                                "No"
                            )
                        }

                    },
                    { flagTemplate -> // Multiple choice of 'what flag is this'
                        QuestionTemplate(
                            "What country is this the flag of?",
                            listOf(
                                flagTemplate.country,
                                /* This generates a random number (as defined is nextNumAnswers) of
                                wrong answers and uses the spread operator (*) to pass them into the
                                listOf call as a flat list rather than nested */
                                *Array(this.stateGen.nextNumAnswers) {
                                    this.flagHandler.nextFlagWhere { it != flagTemplate }.country
                                }
                            ).shuffled(this.stateGen.rng),
                            flagTemplate.country
                        )
                    },
                    { flagTemplate ->
                        QuestionTemplate(
                            "What is the capital city of this country?",
                            listOf(
                                flagTemplate.capital,
                                *Array(this.stateGen.nextNumAnswers) {
                                    this.flagHandler.nextFlagWhere { it != flagTemplate }.capital
                                }
                            ).shuffled(this.stateGen.rng),
                            flagTemplate.capital
                        )
                    },
                    { flagTemplate ->
                        QuestionTemplate(
                            "What continent is this country located in?",
                            listOf(
                                flagTemplate.continent,
                                *Array(this.stateGen.nextNumAnswers) {
                                    this.flagHandler.nextFlagWhere {
                                        it.continent != flagTemplate.continent
                                    }.continent
                                }
                            ).shuffled(this.stateGen.rng),
                            flagTemplate.continent
                        )
                    },
                    { flagTemplate ->
                        QuestionTemplate(
                            "What is the language spoken in this country?",
                            listOf(
                                flagTemplate.language,
                                *Array(this.stateGen.nextNumAnswers) {
                                    this.flagHandler.nextFlagWhere {
                                        it.language != flagTemplate.language
                                    }.language
                                }
                            ).shuffled(this.stateGen.rng),
                            flagTemplate.language
                        )
                    },
                    { flagTemplate -> // Spot the correct country property, or none of the above
                        val noneOfAbove = "None of the above"
                        val correctAnswer: String
                        val fakes = mutableListOf(
                            "Capital: ${
                                this.flagHandler.nextFlagWhere {
                                    it.capital != flagTemplate.capital
                                }.capital
                            }",
                            "Continent: ${
                                this.flagHandler.nextFlagWhere {
                                    it.continent != flagTemplate.continent
                                }.continent
                            }",
                            "Language: ${
                                this.flagHandler.nextFlagWhere {
                                    it.language != flagTemplate.language
                                }.language
                            }"
                        )
                        if (this.stateGen.rng.nextInt(0, 5) == 0) {
                            // None of the above
                            correctAnswer = noneOfAbove
                        } else {
                            // Replace one of the fakes with a real one
                            val reals = listOf(
                                "Capital: ${flagTemplate.capital}",
                                "Continent: ${flagTemplate.continent}",
                                "Language: ${flagTemplate.language}"
                            )
                            val replaceIdx = this.stateGen.rng.nextInt(0, fakes.size)
                            fakes[replaceIdx] = reals[replaceIdx]
                            correctAnswer = reals[replaceIdx]
                        }
                        val answers = fakes.shuffled(this.stateGen.rng).toMutableList().also {
                            it.add(noneOfAbove)
                        }

                        QuestionTemplate(
                            "Which of the following is actually a property of this country?",
                            answers,
                            correctAnswer
                        )
                    }
                )
        }
    }
}