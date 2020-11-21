try {
    val jBase = JSONObject(jsonText)

    val jPageInfo = jBase.getJSONObject("pageInfo")
    val pageName = jPageInfo.getString("pageName")
    val pagePic = jPageInfo.getString("pagePic")

    val jPostsList = JBase.getJSONArray("posts")
    val jPost = jPostsList.getJSONObject(0)
    val postId = jPost.getString("post_id")

} catch (jsonE: JSONException) {
    // Handle exception
}