package com.example.onlysends_compose.ui.home.fake_data
//Fake data used for initial testing. Can probably delete
data class FollowsUser(
    val id: Int,
    val name: String,
    val profileUrl: String,
    val isFollowing: Boolean = false
)

val sampleUsers = listOf(
    FollowsUser(
        id = 1,
        name = "Mr Dip",
        profileUrl = "https://picsum.photos/200"
    ),
    FollowsUser(
        id = 2,
        name = "John Cena",
        profileUrl = "https://picsum.photos/200"
    ),
    FollowsUser(
        id = 3,
        name = "Cristiano",
        profileUrl = "https://picsum.photos/200"
    ),
    FollowsUser(
        id = 4,
        name = "L. James",
        profileUrl = "https://picsum.photos/200"
    )
)