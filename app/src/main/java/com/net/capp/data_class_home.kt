package com.net.capp

data class data_class_home(
    val success: Boolean,
    val data: MutableList<dataHomeItem>
)
data class dataHomeItem(
    val id: String,
    val userid: String,
    val image: List<String>,
    val video: String?,
    val groupid: String,
    val comment: String,
    val district: String,
    val finaldate: String,
    val advertise: advertise,
    var like: String,
    var totallike: Int,
    val user: String,
    val userimage: String,
    val group: String,
    val totalViewCount: Int,
    val isFollowed: List<String>
)

data class advertise(
    val id: String,
    val title: String,
    val image: String,
    val video: String,
    val district: String,
    val expirydate: String
)

data class getUserNews(
    val success: Boolean,
    val data: MutableList<userNewsItem>
)
data class userNewsItem(
    val id: String,
    val user: String,
    val userid: String,
    val image: List<String>,
    val userimage: String,
    val group: String,
    val groupid: String,
    val video: String,
    val finaldate: String,
    val comment: String,
    var like: String,
    var totallike: Int
)

data class data_class_search(
    val success: Boolean,
    val data: List<searchItem>
)
data class searchedItem(
    val search: String
)

data class searchItem(
    val id: String,
    val user: String,
    val userid: String,
    val image: String,
    val video: String?,
    val userimage: String,
    val isFollowed: List<String>,
    val totalViewCount: Int,
    val group: String,
    val finaldate: String,
    val comment: String,
    var totallike: Int
)

data class data_class_NewsDetail(
    val success: Boolean,
    val resp: respond,
    val data: List<newsItem>
)

data class respond(
    val id: String,
    val user: String,
    val image: List<String>,
    val group: String,
    val video: String?,
    val userimage: String,
    val totalViewCount: Int,
    val finaldate: String,
    val comment: String,
    val totallike: Int
)

data class newsItem(
    val id: String,
    val user: String,
    val image: List<String>,
    val group: String,
    val finaldate: String,
    val comment: String,
    val totallike: Int
)

data class postlike(
    val newsid: String,
    val userid: String
)

data class postlikeComment(
    val commentid: String,
    val userid: String
)

data class LikeResponse(
    val success: Boolean,
    val message: String,
    val data: likeDataItem
)
data class likeDataItem(
    val like: String,
    val totallike: Int,
)

data class postNews(
    val image: String,
    val userid: String,
    val groupid: String,
    val comment:String,
    val districtId:String,
    val vedio: String
)

data class postComment(
    val newsid: String,
    val userid: String,
    val comment: String
)

data class profileInsert(
    val id: String,
    val username : String,
    val phone: String,
    val gender: String,
    val dob: String,
    val about: String,
    val state: String,
    val city: String,
    val police: String,
    val image: String
)

data class UserData(
    val success: Boolean,
    val data: UserDataDetails
)

data class UserDataDetails(
    val user: User,
    val postcount: Int,
    val followersCount: Int,
    val followers: List<Follower>,
    val followedCount: Int,
    val followed: MutableList<Follower>
)

data class User(
    val id: String,
    val username: String,
    val phone: String,
    val date: String,
    val status: String,
    val gender: String,
    val dob: String,
    val about: String,
    val state: String,
    val city: String,
    val image: String,
    val isFollowed: List<String>
)
data class Follower(
    val id: String,
    val username: String,
    val image: String,
    val about: String?,
    val follow: Int
)
data class toggleFollow(
    val user_id: String,
    val followBy: String
)

data class updateNews(
    val id: String,
    val userid: String,
    val groupid: String,
    val comment: String,
    val image: String,
    val video: String
)

data class isSuccess(
    val success: Boolean
)

data class commentData(
    val resp: MutableList<commentItem>
)
data class commentItem(
    val id: String,
    val newsid: String,
    val userid: String,
    val comment: String,
    val like: String,
    val name: String
)








