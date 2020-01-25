package net.unsweets.sample

import net.unsweets.gamma.BuildConfig
import net.unsweets.gamma.domain.entity.Poll
import net.unsweets.gamma.util.RandomID
import java.util.*

object Polls {
    val poll1
        get() = Poll(
        Date(),
        Date(),
            RandomID.get,
        isAnonymous = true,
        isPublic = true,

        maxOptions = 1,
        options = listOf(
            Poll.PollOption("choice 1", 1),
            Poll.PollOption("choice 2", 2),
            Poll.PollOption("choice 3", 3),
            Poll.PollOption("choice 4", 4),
            Poll.PollOption("choice 5", 5),
            Poll.PollOption("choice 6", 6),
            Poll.PollOption("choice 7", 7),
            Poll.PollOption("choice 8", 8),
            Poll.PollOption("choice 9", 9),
            Poll.PollOption("choice 10", 10)
        ),
        pollToken = "token",
        prompt = "prompt",
        source = Clients.testClient,
        type = BuildConfig.APPLICATION_ID
    )
}