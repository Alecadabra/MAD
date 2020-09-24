package com.alec.mad.assignment1.state

import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.model.Flag
import com.alec.mad.assignment1.model.Question
import kotlin.random.Random

class GameState {
    /**
     * Immutable storage for flags.
     */
    val flags: List<Flag> = Init.initFlags()

    /**
     * The player's mutable point count. Changes are observed and used to update playerCondition.
     */
    var playerPoints: Int = Init.initPlayerPoints()
        set(value) {
            // Update player condition
            playerCondition = when {
                value <= 0 -> PlayerCondition.LOST
                value >= targetPoints -> PlayerCondition.WON
                else -> PlayerCondition.PLAYING
            }

            field = value

            // Notify observers
            observers.forEach { it.onUpdatePlayerPoints(value) }
        }

    /**
     * No. of points to reach to win.
     */
    val targetPoints: Int = Init.initTargetPoints(playerPoints)

    /**
     * The player's state. Automatically set when playerPoints is changed.
     */
    var playerCondition: PlayerCondition = PlayerCondition.PLAYING
        private set(value) {
            if (field != value) {
                // Only notify if value changes

                // Notify observers
                this.observers.forEach { it.onUpdatePlayerCondition(value) }
            }

            field = value
        }

    val observers: MutableSet<GameStateObserver> = mutableSetOf()

    /**
     * The player's state. This is automatically updated when playerPoints is updated.
     */
    enum class PlayerCondition {
        /** Player is currently playing the game */
        PLAYING,

        /** Player has reached the target points */
        WON,

        /** Player's points have decreased to zero. */
        LOST;
    }

    /**
     * Used to initialise the game state.
     */
    private object Init {

        /**
         * Builds a list of flags.
         */
        fun initFlags(): List<Flag> = Flags.getFlags(this::initFlagQuestions)

        /**
         * Gets a player's starting points.
         */
        fun initPlayerPoints(): Int = Rand.nextStartingPoints

        /**
         * Gets a player's target points.
         */
        fun initTargetPoints(playerPoints: Int): Int = Rand.getNextTargetPoints(playerPoints)

        /**
         * Builds a list of questions for a flag of given name.
         */
        fun initFlagQuestions(flagName: String) = Rand.getNextFlagQuestions(flagName)

        /**
         * Holds the information necessary to create a flag.
         */
        private class FlagTemplate(val name: String, val drawableId: Int)

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
        private object Rand {

            // Constants for ranges of different randomly generated values
            const val STARTING_POINTS_LOWER = 0
            const val STARTING_POINTS_UPPER = 10
            const val TARGET_POINTS_UPPER = 20
            const val NO_QUESTIONS_LOWER = 5
            const val NO_QUESTIONS_UPPER = 10
            const val QUESTION_POINTS_LOWER = 5
            const val QUESTION_POINTS_UPPER = 15
            const val QUESTION_PENALTY_LOWER = 0
            const val QUESTION_PENALTY_UPPER = 10
            const val QUESTIONS_OPTIONS = 3
            const val QUESTION_SPECIAL_PROB = 5
            const val QUESTION_ANSWERS_LOWER = 2
            const val QUESTION_ANSWERS_UPPER = 4

            /** Random number generator */
            val rng: Random = Random.Default

            /** Get the next value for player starting points */
            val nextStartingPoints
                get() = rng.nextInt(
                    STARTING_POINTS_LOWER,
                    STARTING_POINTS_UPPER + 1
                )

            fun getNextTargetPoints(playerPoints: Int): Int = rng.nextInt(
                playerPoints + 1, TARGET_POINTS_UPPER
            )

            fun getNextFlagQuestions(flagName: String) = Questions.getQuestions(flagName)

            /** Get the next value for the number of questions for a flag */
            val nextNoQuestions get() = rng.nextInt(NO_QUESTIONS_LOWER, NO_QUESTIONS_UPPER + 1)

            /** Get the next value for the point value of a question */
            val nextQuestionPoints
                get() = rng.nextInt(
                    QUESTION_POINTS_LOWER,
                    QUESTION_POINTS_UPPER + 1
                )

            /** Get the next value for the penalty value of a question */
            val nextQuestionPenalty
                get() = rng.nextInt(
                    QUESTION_PENALTY_LOWER,
                    QUESTION_PENALTY_UPPER + 1
                )

            /** Get the next value for the isSpecial of a question */
            val nextQuestionIsSpecial get() = rng.nextInt(0, QUESTION_SPECIAL_PROB) == 0

            /** Get the next value to use to select the question initializer to use for a question */
            val nextQuestionOption get() = rng.nextInt(0, QUESTIONS_OPTIONS)

            /** Get the next number of answers for a question to have */
            val nextNumAnswers
                get() = rng.nextInt(
                    QUESTION_ANSWERS_LOWER,
                    QUESTION_ANSWERS_UPPER + 1
                )
        }

        /**
         * Handles flags.
         */
        private object Flags {
            /** List of pairs of name, drawable of each flag */
            val flagTemplates = listOf(
                FlagTemplate("Australia", R.drawable.flag_au),
                FlagTemplate("Canada", R.drawable.flag_ca),
                FlagTemplate("Bulgaria", R.drawable.flag_bg),
                FlagTemplate("The Czech Republic", R.drawable.flag_cz),
                FlagTemplate("Germany", R.drawable.flag_de),
                FlagTemplate("Spain", R.drawable.flag_es),
                FlagTemplate("France", R.drawable.flag_fr),
                FlagTemplate("Hong Kong", R.drawable.flag_hk),
                FlagTemplate("Japan", R.drawable.flag_jp),
                FlagTemplate("South Korea", R.drawable.flag_kr),
                FlagTemplate("Malaysia", R.drawable.flag_my),
                FlagTemplate("The Netherlands", R.drawable.flag_nl),
                FlagTemplate("Poland", R.drawable.flag_pl),
                FlagTemplate("Russia", R.drawable.flag_ru),
                FlagTemplate("The United Kingdom", R.drawable.flag_uk),
                FlagTemplate("USA", R.drawable.flag_us)
            ).shuffled(Rand.rng)

            private var flagIterator: Iterator<FlagTemplate> = flagTemplates.iterator()

            /**
             * Gets the next flag (As a pair of name, drawable) that matches the given condition.
             *
             * This will recurse infinitely if no match is found.
             */
            fun nextFlagWhere(check: (FlagTemplate) -> Boolean): FlagTemplate {
                if (!flagIterator.hasNext()) {
                    // If the iterator is at it's end, reshuffle the flags and loop back around
                    flagIterator = flagTemplates.shuffled(Rand.rng).iterator()
                }

                val flag = flagIterator.next()

                return if (check(flag)) {
                    flag
                } else {
                    //  Recursion!
                    nextFlagWhere(check)
                }
            }

            fun getFlags(qnGenerator: (String) -> List<Question>) =
                flagTemplates.map { flagTemplate ->
                    Flag(flagTemplate.name, qnGenerator(flagTemplate.name), flagTemplate.drawableId)
                }
        }

        /**
         * Handles questions.
         */
        private object Questions {
            /**
             * Generates a list of questions for the flag of given name.
             */
            fun getQuestions(flagName: String) = List(Rand.nextNoQuestions) { idx ->
                val qnTemplate = questionTemplateGenerators.random(Rand.rng)(flagName)
                Question(
                    idx + 1,
                    Rand.nextQuestionPoints,
                    Rand.nextQuestionPenalty,
                    Rand.nextQuestionIsSpecial,
                    qnTemplate.questionText,
                    qnTemplate.answers,
                    qnTemplate.correctAnswer
                )
            }

            /**
             * Holds some lambdas that take in a flag's name and generate a question template for
             * that flag.
             */
            val questionTemplateGenerators = setOf<(flagName: String) -> QuestionTemplate>(
                { flagName ->
                    QuestionTemplate( // 'Is this the flag of x' where the answer is yes
                        "Is this the flag of $flagName?",
                        listOf("Yes", "No"),
                        "Yes"
                    )
                },
                { flagName ->
                    QuestionTemplate( // 'Is this the flag of x' where the answer is no
                        "Is this the flag of ${Flags.nextFlagWhere { it.name != flagName }.name}?",
                        listOf("Yes", "No"),
                        "No"
                    )
                },
                { flagName ->
                    QuestionTemplate( // Multiple choice of 'what flag is this'
                        "What country is this the flag of?",
                        listOf(
                            flagName,
                            /* This generates a random number (as defined is nextNumAnswers) of
                            wrong answers and uses the spread operator (*) to pass them into the
                            listOf call as a flat list rather than nested */
                            *Array(Rand.nextNumAnswers) {
                                Flags.nextFlagWhere { it.name != flagName }.name
                            }
                        ).shuffled(Rand.rng),
                        flagName
                    )
                }
            )
        }
    }
}