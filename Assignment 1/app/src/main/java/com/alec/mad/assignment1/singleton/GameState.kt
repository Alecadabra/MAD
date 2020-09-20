package com.alec.mad.assignment1.singleton

import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.fragment.StatsBarFragment
import com.alec.mad.assignment1.model.Flag
import com.alec.mad.assignment1.model.Question
import kotlin.random.Random

object GameState {
    /**
     * Immutable storage for flags.
     *
     * @see com.alec.mad.assignment1.model.Flag
     */
    val flags: List<Flag> = Init.initFlags()

    /**
     * The player's mutable point count. Changes are observed and used to update playerCondition.
     *
     * @see com.alec.mad.assignment1.GameState.playerCondition
     */
    var playerPoints: Int = Init.initPlayerPoints()
        set(value) {
            // Update player condition
            playerCondition = when {
                value <= 0                 -> PlayerCondition.LOST
                value >= targetPoints -> PlayerCondition.WON
                else                       -> PlayerCondition.PLAYING
            }

            field = value

            // Notify the stats bar
            StatsBarFragment.onUpdatePoints()
        }

    /**
     * No. of points to reach to win.
     *
     * @see com.alec.mad.assignment1.GameState.playerPoints
     */
    val targetPoints: Int = Init.initTargetPoints(playerPoints)

    /**
     * The player's state. Automatically set when playerPoints is changed.
     *
     * @see com.alec.mad.assignment1.GameState.PlayerCondition
     * @see com.alec.mad.assignment1.GameState.playerPoints
     */
    var playerCondition: PlayerCondition = PlayerCondition.PLAYING
        private set

    /**
     * The player's state, either PLAYING (If normally playing the game), WON (If reached the
     * targetPoints) or LOST (If decreased points to 0). This is automatically updated when
     * playerPoints is updated.
     */
    enum class PlayerCondition {
        PLAYING,
        WON,
        LOST;
    }

    /**
     * Used to initialise the game state
     */
    private object Init {
        // Constants for ranges of different randomly generated values
        private const val STARTING_POINTS_LOWER = 0
        private const val STARTING_POINTS_UPPER = 10
        private const val TARGET_POINTS_UPPER = 20
        private const val NO_QUESTIONS_LOWER = 5
        private const val NO_QUESTIONS_UPPER = 10
        private const val QUESTION_POINTS_LOWER = 5
        private const val QUESTION_POINTS_UPPER = 15
        private const val QUESTION_PENALTY_LOWER = 0
        private const val QUESTION_PENALTY_UPPER = 10
        private const val QUESTIONS_OPTIONS = 3
        private const val QUESTION_SPECIAL_PROB = 5

        /** Random number generator */
        private val rng: Random = Random.Default

        /** Get the next value for player starting points */
        private val nextStartingPoints get() = rng.nextInt(STARTING_POINTS_LOWER, STARTING_POINTS_UPPER + 1)
        /** Get the next value for the number of questions for a flag */
        private val nextNoQuestions get() = rng.nextInt(NO_QUESTIONS_LOWER, NO_QUESTIONS_UPPER + 1)
        /** Get the next value for the point value of a question */
        private val nextQuestionPoints get() = rng.nextInt(QUESTION_POINTS_LOWER, QUESTION_POINTS_UPPER + 1)
        /** Get the next value for the penalty value of a question */
        private val nextQuestionPenalty get() = rng.nextInt(QUESTION_PENALTY_LOWER, QUESTION_PENALTY_UPPER + 1)
        /** Get the next value for the isSpecial of a question */
        private val nextQuestionIsSpecial get() = rng.nextInt(0, QUESTION_SPECIAL_PROB) == 0
        /** Get the next value to use to select the question initializer to use for a question */
        private val nextQuestionOption get() = rng.nextInt(0, QUESTIONS_OPTIONS)

        /**
         * Builds a list of flags.
         */
        fun initFlags(): List<Flag> = Flags.flags.map {
            Flag(it.first, initFlagQuestions(it.first), it.second)
        }

        /**
         * Gets a player's starting points.
         */
        fun initPlayerPoints(): Int = nextStartingPoints

        /**
         * Gets a player's target points.
         */
        fun initTargetPoints(playerPoints: Int): Int = rng.nextInt(
            playerPoints + 1, TARGET_POINTS_UPPER
        )

        /**
         * Builds a list of questions for a flag of given name.
         */
        fun initFlagQuestions(flagName: String) = List<Question>(nextNoQuestions) { idx ->
            when (nextQuestionOption) {
                0 -> Question( // 'Is this the flag of x' where the answer is yes
                    idx + 1,
                    nextQuestionPoints,
                    nextQuestionPenalty,
                    nextQuestionIsSpecial,
                    "Is this the flag of $flagName?",
                    listOf("Yes", "No"),
                    "Yes"
                )
                1 -> Question( // 'Is this the flag of x' where the answer is no
                    idx + 1,
                    nextQuestionPoints,
                    nextQuestionPenalty,
                    nextQuestionIsSpecial,
                    "Is this the flag of ${Flags.nextFlagWhere { it.first != flagName }.first}?",
                    listOf("Yes", "No"),
                    "No"
                )
                2 -> Question( // Multiple choice of 'what flag is this'
                    idx + 1,
                    nextQuestionPoints,
                    nextQuestionPenalty,
                    nextQuestionIsSpecial,
                    "What country is this the flag of?",
                    listOf(
                        flagName,
                        Flags.nextFlagWhere { it.first != flagName }.first,
                        Flags.nextFlagWhere { it.first != flagName }.first,
                        Flags.nextFlagWhere { it.first != flagName }.first
                    ).shuffled(rng),
                    flagName
                )
                else -> throw IllegalStateException(
                    "Internal error - nextQuestionOption not properly configured"
                )
            }
        }
        /* Alternative
        fun initFlagQuestions(flagName: String) = List<Question>(nextNoQuestions) {
            listOf<(String) -> Question>(
                {
                    Question( // 'Is this the flag of x' where the answer is yes
                        this.nextQuestionPoints,
                        this.nextQuestionPenalty,
                        this.nextQuestionIsSpecial,
                        "Is this the flag of $flagName?",
                        listOf("Yes", "No"),
                        "Yes"
                    )
                },
                {
                    Question( // 'Is this the flag of x' where the answer is no
                        this.nextQuestionPoints,
                        this.nextQuestionPenalty,
                        this.nextQuestionIsSpecial,
                        "Is this the flag of ${Flags.nextFlagWhere { it.first != flagName }.first}?",
                        listOf("Yes", "No"),
                        "No"
                    )
                },
                {
                    Question( // Multiple choice of 'what flag is this'
                        this.nextQuestionPoints,
                        this.nextQuestionPenalty,
                        this.nextQuestionIsSpecial,
                        "What country is this the flag of?",
                        listOf(
                            flagName,
                            Flags.nextFlagWhere { it.first != flagName }.first,
                            Flags.nextFlagWhere { it.first != flagName }.first,
                            Flags.nextFlagWhere { it.first != flagName }.first
                        ).shuffled(this.rng),
                        flagName
                    )
                }
            ).random(rng)(flagName)
        }
         */

        private object Flags {
            /** List of pairs of name, drawable of each flag */
            val flags = listOf<Pair<String, Int>>(
                "Australia" to R.drawable.flag_au,
                "Canada" to R.drawable.flag_ca,
                "Bulgaria" to R.drawable.flag_bg,
                "The Czech Republic" to R.drawable.flag_cz,
                "Germany" to R.drawable.flag_de,
                "Spain" to R.drawable.flag_es,
                "France" to R.drawable.flag_fr,
                "Hong Kong" to R.drawable.flag_hk,
                "Japan" to R.drawable.flag_jp,
                "South Korea" to R.drawable.flag_kr,
                "Malaysia" to R.drawable.flag_my,
                "The Netherlands" to R.drawable.flag_nl,
                "Poland" to R.drawable.flag_pl,
                "Russia" to R.drawable.flag_ru,
                "The United Kingdom" to R.drawable.flag_uk,
                "USA" to R.drawable.flag_us
            ).shuffled(rng)

            private var flagIterator: Iterator<Pair<String, Int>> = flags.iterator()

            /**
             * Gets the next flag (As a pair of name, drawable) that matches the given condition.
             *
             * This will recurse infinitely if no match is found.
             */
            fun nextFlagWhere(check: (Pair<String, Int>) -> Boolean): Pair<String, Int> {
                if (!flagIterator.hasNext()) {
                    // If the iterator is at it's end, reshuffle the flags and loop back around
                    flagIterator = flags.shuffled(rng).iterator()
                }

                val flag = flagIterator.next()

                return if (check(flag)) {
                    flag
                } else {
                    //  Recursion!
                    nextFlagWhere(check)
                }
            }
        }
    }
}