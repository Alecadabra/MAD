# Take-Home Examination

## Question 1

### (a)

Explicit intents specify an exact application, eg. an `Activity` class that will
satisfy the intent, such as when you are starting your own Activity from
withing your app.

Eg. (Kotlin)

```kotlin
val intent = Intent(context, MyOtherActivity.javaClass)
startActivity(intent)
```

Implicit intents specify a general action for some component of an app on the
device to fulfill, such as when you want to show some sort of information
to the user and use `Intent.ACTION_VIEW`.

Eg. (Kotlin)

```kotlin
val lat = ...
val long = ...
val intent = Intent(Intent.ACTION_VIEW)
intent.data = Uri.parse("geo:$lat,$long")
startActivity(intent)
```

### (b)

 * Activity B has a `getIntent()` static function that takes in the `Student`
 object (and the context of the calling activity) and places it in an explicit
 intent that it returns, and Activity A starts the activity with this Intent.
 When Activity B is created, it can  retrieve the student object from the
 intent.

 * Activity A places the `Student` object in a singleton and starts Activity
 B as normal. When Activity B is created it can get the object from the
 singleton.

### (c)

 * When the device's screen is rotated.

 * When a new activity is started and the Android system needs to de-allocate
 the resources of the background activity.

To save the `Club` class, provide a `Parcelable` implementation in both the
`Student` and `Club` classes. The `Student` class can save it's `id` and
`name` fields into the parcel trivially. The `Club` class can save the `name`
field into the parcel trivially and save the `members` field in the parcel as an
array list of parcelables.

### (d)

In a Fragment, `findViewById()` is called on the reference to the view, whereas
in an Activity, it is called on the Activity reference (`this`).

Eg. (Kotlin)

```kotlin
// In an Activity
findViewById(R.id.etc)

// In a fragment
view.findViewById(R.id.etc)
```

### (e)

A view that contains other children Views, for example in a RecyclerView,
each view holder has a `ViewGroup` with all of the views for that view holder.

## Question 2

### (a)

`onCreate()` is called at the start of the lifecycle when the Activity or
Fragment is created. This is where the state should be initialised.

`onPause()` is called when the user is leaving the current activity or fragment
and the current one is paused. The activity or fragment may or may not
be resumed later.

`onSaveInstance()` is called when an activity or fragment begins to stop, so it
can save it's state to a bundle that can be retrieved in `onCreate()`.

`onStop()` is called when the activity or fragment enters the 'Stopped' state
and is no longer visible to the user. If this function is called, it will
always be after `onPause()`.

`onDestroy()` is called when the activity or fragment is about to be destroyed,
if this is called, it will always be after `onStop()`.

### (b)

See below or `Q2_b.xml`.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- Left-most frame (Fragment_A) -->
    <FrameLayout
        android:id="@+id/frame1"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toStartOf="@id/space"/>

    <!-- Middle spacer -->
    <Space
        android:id="@+id/space"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toStartOf="@id/frame2"
        app:layout_constraintStart_toEndOf="@id/frame1" />

    <!-- Right-most frame (Fragment_B) -->
    <FrameLayout
        android:id="@+id/frame2"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toEndOf="@id/space"/>

    <!-- Using 0dp means that the layout height is essentially ignored and the
        height is determined by the constraints. -->

</androidx.constraintlayout.widget.ConstraintLayout>
```

### (c)

See below or `Q2_c.kt`.

(Kotlin)

```kotlin
class MyListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_item_list,
            container,
            false
        ) as? RecyclerView ?: error("View not recycler view")

        // Set RV params
        view.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        view.adapter = MyListAdapter()

        return view
    }

    class MyListAdapter
        : RecyclerView.Adapter<MyListAdapter.MyListViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyListViewHolder {
            // Create the view and view holder from it
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_item, parent, false
            )
            return MyListViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: MyListViewHolder,
            position: Int
        ) {
            // Set text in the view holder
            @SuppressLint("SetTextI18n")
            val text = "Button ${position + 1}"
            holder.textView.text = text
            holder.textView.setOnClickListener { Log.i("Button", text) }
        }

        // Show 100 items
        override fun getItemCount(): Int = 100

        inner class MyListViewHolder(
            view: View
        ) : RecyclerView.ViewHolder(view) {
            // Text field
            val textView: TextView = view.findViewById(R.id.textView)
        }
    }
}
```

### (d)

 * Use frame layouts to have multiple fragments of different sizes visible.

 * Use fragment transactions to do multiple simultaneous changes to the UI.

## Question 3

### (a)

See below or `Q3_a.kt`.

(Kotlin)

#### i)

```kotlin
private fun pickContact() {
    val intent = Intent(
        Intent.ACTION_PICK,
        ContactsContract.Contacts.CONTENT_URI
    )
    startActivityForResult(intent, PICK_CONTACT_REQUEST)
}

companion object {
    const val PICK_CONTACT_REQUEST = 1
}
```

#### ii)

```kotlin
override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == Activity.RESULT_OK) {
        when (requestCode) {
            PICK_CONTACT_REQUEST -> getEmail(data)
        }
    }
}

private fun getEmail(intent: Intent?): String {
    // Get ID
    val id: Int = intent?.data?.let { uri ->
        var nullableId: Int? = null
        
        contentResolver?.query(
            uri,
            arrayOf(ContactsContract.Contacts._ID),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                nullableId = cursor.getInt(0)
            }
        }
        return@let nullableId
    } ?: error("Query failed")

    // Return email or "None"
    return contentResolver?.let { contentResolver ->
        var nullableEmail: String? = null

        contentResolver.query(
            CommonDataKinds.Email.CONTENT_URI,
            arrayOf(CommonDataKinds.Email.ADDRESS),
            "${CommonDataKinds.Email.CONTACT_ID} = ?",
            arrayOf("$id"),
            null,
            null
        )?.use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                nullableEmail = cursor.getString(0)
            }
        }

        return@let nullableEmail
    } ?: "None"
}
```

### (b)

See below or `Q3_b.kt`.

(Kotlin)

#### i)

```kotlin
fun createTable(db: SQLiteDatabase) {
    val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols
    db.execSQL(
        """
            CREATE TABLE ${GameScoreInfoSchema.GameScoreInfoTable.NAME} (
                ${cols.ID} INTEGER,
                ${cols.NAME} TEXT,
                ${cols.SCORE} INTEGER
            )
        """.trimIndent()
    )
}
```

#### ii)

```kotlin
fun insert(info: GameScoreInfo) {
    val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols
    this.writableDatabase.insert(
        GameScoreInfoSchema.GameScoreInfoTable.NAME,
        null,
        ContentValues().also { cv ->
            cv.put(cols.ID, info.id)
            cv.put(cols.NAME, info.name)
            cv.put(cols.SCORE, info.score)
        }
    )
}
```

### iii)

```kotlin
// Essentially the kotlin Pair class, just holds a score and a name in an
// easily accessible way.
data class ScoreAndName(val score: Int, val name: String)

// Returns a collection of the ScoreAndName data class.
fun getScoresAndNames(): Collection<ScoreAndName> {

    val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols

    // Cursor class
    class GameScoreInfoCursor(cursor: Cursor) : CursorWrapper(cursor) {
        val scoreAndName: ScoreAndName
            get() = ScoreAndName(
                score = getInt(getColumnIndex(cols.SCORE)),
                name = getString(getColumnIndex(cols.NAME))
            )
    }

    val scoreAndNames = mutableSetOf<ScoreAndName>()

    GameScoreInfoCursor(
        this.writableDatabase.query(
            GameScoreInfoSchema.GameScoreInfoTable.NAME,
            null, null, null, null, null, null
        )
    ).use { cur -> 
        cur.moveToFirst()
        while (!cur.isAfterLast) {
            scoreAndNames.add(cur.scoreAndName)
            cur.moveToNext()
        }
    }
    
    return scoreAndNames
}
```

### (c)

It allows us to take full size photos rather than thumbnail photos.

The `FileProvider` is a `ContentProvider` that handles the storage
and retrival of the full sized photo.

We give the file provider to the camera app as a Uri and give the camera app
access to it.

## Question 4

### (a)

I made a simple singleton with a download function that sets a field.

I assumed there is no certificate stuff to handle.

See below or `Q4_a.kt`.

(Kotlin)

```kotlin
object Downloader {
    
    var time: String? = null

    // Sets the above time field
    fun downloadTime() {
        // Run in an IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val connection = try {
                // Open and connect to URL
                URL("https://a.b.c.d/time").openConnection().also { 
                    it.connect() 
                } as HttpsURLConnection
            } catch (ioException: IOException) {
                // Log error and make connection null
                Log.e("Downloader", "Failed to connect to URL", ioException)
                null
            }

            val localTime = if (
                connection != null &&
                connection.responseCode == HttpsURLConnection.HTTP_OK
            ) {
                try {
                    // Read everything into a string
                    connection.inputStream.bufferedReader().readText()
                } catch (ioE: IOException) {
                    // Log error and make time null
                    Log.e("Downloader", "Failed to download", ioE)
                    null
                } finally {
                    connection.disconnect()
                }
            } else null // If connection failed time is null

            withContext(Dispatchers.Main) {
                // Set field
                this@Downloader.time = localTime
            }
        }
    }
}
```

### (b)

`doInBackground()` runs in a different thread to `onPostExecute()`, which
runs on the GUI's thread so can pass data around a Fragment or Activity to
have the data displayed.

### (c)

See below or `Q4_c.kt`.

(Kotlin)

```kotlin
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
```

## Question 5

### (a)

Advantage: A web app can run on basically any device with internet access.

Disadvantage: Web apps must be written in JavaScript and account for the
lowest common denominator web browsers.

### (b)

TypeScript is a superset language of JavaScript that adds optional typing to
variables among other things and transpiles to an old version of JavaScript.

 * It allows the compiler to check for type mismatch errors. So has increased
 Defence in Depth :).

 * It transpiles to a version of JS that will work on old browsers. So has
 better compatability than modern JS.

### (c)

False. Both can operate without client-server communication.

For example, a simple calculator mobile or web app. There is no need for a
server.

### (d)

See below or `Q5_d.js`.

(JavaScript)

```js
$(window).on(
    "load",
    () => {
        $("#theButton").on(
            "click",
            () => {
                let number = parseFloat($("#theNumberField").val())

                if (isNaN(number)) {
                    alert("You must enter a number")
                } else {
                    $("#theParagraph").text(`${number * 10}`)
                }
            }
        )
    }
)
```

### (e)

See below or `Q5_e.js`.

(JavaScript)

```js
$(window).on(
    "load",
    () => {
        $("#theButton").on(
            "click",
            () => {
                fetch(
                    "/data",
                    { method: "GET" }
                ).then(
                    response => {
                        if (response.ok) {
                            return response.text();
                        } else {
                            throw new Error(response.statusText);
                        }
                    }
                ).then(
                    data => {
                        $("#theNumberField").text(data);
                    }
                ).catch(
                    err => alert(err)
                );
            }
        );
    }
);
```