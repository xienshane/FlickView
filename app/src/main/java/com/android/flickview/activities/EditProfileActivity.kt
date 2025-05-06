package com.android.flickview.activities

import android.app.Activity // Keep RESULT_OK
import android.net.Uri
import android.os.Bundle
import android.util.Log // Ensure Log is imported
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager // Import Cloudinary
import com.cloudinary.android.callback.ErrorInfo // Import Cloudinary
import com.cloudinary.android.callback.UploadCallback // Import Cloudinary
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
class EditProfileActivity : AppCompatActivity() {

    // Firebase Services
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profileImage: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var editImageButton: ImageButton
    private lateinit var saveChangesButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var backButton: ImageButton
    private var selectedImageUri: Uri? = null
    private var currentProfileImageUrlFromFirestore: String? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private val TAG = "EditProfileActivity" // Tag for Logcat filtering
    private val CLOUDINARY_UNSIGNED_UPLOAD_PRESET = "flickview_user_profiles" // <--- REPLACE THIS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_page)
        Log.d(TAG, "onCreate: Activity starting.")

        // Initialize Firebase
        auth = Firebase.auth
        db = Firebase.firestore
        Log.d(TAG, "onCreate: Firebase services initialized.")
        bindViews()
        Log.d(TAG, "onCreate: Views bound.")
        setupListeners()
        Log.d(TAG, "onCreate: Listeners set up.")
        setupImagePicker()
        Log.d(TAG, "onCreate: Image picker set up.")
        loadCurrentProfileData()
    }

    private fun bindViews() {
        backButton = findViewById(R.id.buttonback)
        profileImage = findViewById(R.id.profile_image)
        editImageButton = findViewById(R.id.edit_image_button)
        usernameEditText = findViewById(R.id.et_name)
        passwordEditText = findViewById(R.id.et_password)
        saveChangesButton = findViewById(R.id.button_save)
        cancelButton = findViewById(R.id.button_cancel)
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }
        editImageButton.setOnClickListener {
            Log.d(TAG, "Edit image button clicked.")
            openImageChooser()
        }
        saveChangesButton.setOnClickListener {
            Log.d(TAG, "Save changes button clicked.")
            saveProfileChanges()
        }
        cancelButton.setOnClickListener { finish() }
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Log.i(TAG, "Image selected via picker. URI: $uri") // Use Info level for successful selection
                selectedImageUri = uri
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(profileImage)
            } else {
                Log.w(TAG, "Image picker returned null URI.")
            }
        }
    }

    private fun openImageChooser() {
        Log.d(TAG, "openImageChooser: Launching image picker.")
        // Launches the system's file chooser to select an image
        imagePickerLauncher.launch("image/*")
    }

    private fun loadCurrentProfileData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "loadCurrentProfileData: Current user is NULL. Cannot load data.")
            Toast.makeText(this, "Error: User not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.i(TAG, "loadCurrentProfileData: Starting load for UID: ${currentUser.uid}") // Info level for starting load

        val authUsername = currentUser.displayName
        if (!authUsername.isNullOrBlank()) {
            usernameEditText.setText(authUsername)
            Log.d(TAG, "loadCurrentProfileData: Username loaded from Firebase Auth: $authUsername")
        } else {
            Log.d(TAG, "loadCurrentProfileData: Username is null/blank in Firebase Auth. Will check Firestore.")
        }

        Log.d(TAG, "loadCurrentProfileData: Fetching Firestore document: users/${currentUser.uid}")
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.i(TAG, "loadCurrentProfileData: Firestore document found and exists.") // Info for success
                    // Load username from Firestore if it wasn't set from Auth
                    if (usernameEditText.text.isNullOrBlank()) {
                        val firestoreUsername = documentSnapshot.getString("username") ?: ""
                        usernameEditText.setText(firestoreUsername)
                        Log.d(TAG, "loadCurrentProfileData: Username loaded from Firestore: '$firestoreUsername'")
                    }

                    currentProfileImageUrlFromFirestore = documentSnapshot.getString("profileImageUrl")
                    Log.d(TAG, "loadCurrentProfileData: Loaded profileImageUrl from Firestore: '$currentProfileImageUrlFromFirestore'")

                    Glide.with(this@EditProfileActivity)
                        .load(currentProfileImageUrlFromFirestore) // Load Cloudinary URL
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                            override fun onLoadFailed(e: com.bumptech.glide.load.engine.GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?, isFirstResource: Boolean): Boolean {
                                Log.w(TAG, "loadCurrentProfileData: Glide failed to load image from URL: $currentProfileImageUrlFromFirestore", e)
                                return false
                            }
                            override fun onResourceReady(resource: android.graphics.drawable.Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                                Log.d(TAG, "loadCurrentProfileData: Glide successfully loaded image from URL: $currentProfileImageUrlFromFirestore")
                                return false
                            }
                        })
                        .into(profileImage)

                } else {
                    Log.w(TAG, "loadCurrentProfileData: Firestore document for UID ${currentUser.uid} is null or does not exist.")
                    Glide.with(this@EditProfileActivity).load(R.drawable.user).into(profileImage) // Ensure placeholder
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "loadCurrentProfileData: Firestore get() FAILED for users/${currentUser.uid}", e) // Error level for failure
                Toast.makeText(this, "Failed to load profile data.", Toast.LENGTH_SHORT).show()
                Glide.with(this@EditProfileActivity).load(R.drawable.user).into(profileImage) // Ensure placeholder
            }
    }

    private fun saveProfileChanges() {
        Log.d(TAG, "saveProfileChanges: Save process initiated.")
        val newUsername = usernameEditText.text.toString().trim()
        val newPassword = passwordEditText.text.toString()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "saveProfileChanges: Current user is NULL. Aborting save.")
            Toast.makeText(this, "Error: Cannot save, user not found.", Toast.LENGTH_SHORT).show()
            setEditingEnabled(true)
            return
        }
        Log.d(TAG, "saveProfileChanges: User confirmed: ${currentUser.uid}")
        Log.d(TAG, "saveProfileChanges: Data to save - Username: '$newUsername', Password set: ${newPassword.isNotEmpty()}, Image URI: $selectedImageUri")


        if (!isProfileDataValid(newUsername, newPassword)) {
            Log.w(TAG, "saveProfileChanges: Validation FAILED.") // Warn for validation failure
            return // Stop if validation fails
        }
        Log.d(TAG, "saveProfileChanges: Validation passed.")

        setEditingEnabled(false)
        Toast.makeText(this, "Saving profile...", Toast.LENGTH_SHORT).show() // Indicate activity

        // Determine what needs updating
        val isImageChanged = selectedImageUri != null
        val isPasswordChanged = newPassword.isNotEmpty()
        Log.d(TAG, "saveProfileChanges: Flags - isImageChanged: $isImageChanged, isPasswordChanged: $isPasswordChanged")

        // Username change needs careful comparison against both Auth and potential Firestore value
        Log.d(TAG, "saveProfileChanges: Checking if username actually changed...")
        checkIfUsernameChanged(currentUser.uid, newUsername) { isUsernameActuallyChanged ->
            Log.i(TAG, "saveProfileChanges: Username change check result: $isUsernameActuallyChanged") // Info for check result

            // --- Main Update Flow ---
            if (isImageChanged) {
                Log.d(TAG, "saveProfileChanges: Image HAS changed. Starting image upload flow.")
                // If image changed, upload it first, then update Firebase in the callback
                uploadImageToCloudinaryAndUpdateFirebase(currentUser.uid, newUsername, selectedImageUri!!) { success, _ -> // Don't need the URL back here directly
                    Log.d(TAG, "saveProfileChanges: uploadImageToCloudinaryAndUpdateFirebase callback received. Success: $success")
                    if (success) {
                        // Image uploaded and Firebase (Auth name + Firestore name/URL) updated
                        if (isPasswordChanged) {
                            Log.d(TAG, "saveProfileChanges: Image flow successful. Now updating password.")
                            updatePasswordIfNeeded(newPassword) { passwordSuccess ->
                                Log.d(TAG, "saveProfileChanges: updatePasswordIfNeeded (after image) callback received. Success: $passwordSuccess")
                                handleFinalResult(passwordSuccess, "Profile updated, but password change failed.")
                            }
                        } else {
                            Log.i(TAG, "saveProfileChanges: Image flow successful. No password change needed.") // Info for successful path
                            handleFinalResult(true, null) // All successful
                        }
                    } else {
                        // Image upload or subsequent Firebase update failed
                        Log.e(TAG, "saveProfileChanges: uploadImageToCloudinaryAndUpdateFirebase FAILED.") // Error level
                        handleFinalResult(false, "Failed to update profile (image or database error).")
                    }
                }
            } else { // No Image Change
                Log.d(TAG, "saveProfileChanges: Image has NOT changed. Checking username/password changes.")

                if (!isUsernameActuallyChanged && isPasswordChanged) {
                    Log.d(TAG, "saveProfileChanges: Only password needs updating.")
                    updatePasswordIfNeeded(newPassword) { passwordSuccess ->
                        Log.d(TAG, "saveProfileChanges: updatePasswordIfNeeded (no image/user change) callback received. Success: $passwordSuccess")
                        handleFinalResult(passwordSuccess, if (!passwordSuccess) "Password update failed." else null)
                    }
                } else if (isUsernameActuallyChanged) {
                    Log.d(TAG, "saveProfileChanges: Username HAS changed (no image change). Starting username update flow.")
                    // Update Firebase (Auth name + Firestore name). Pass null for image URL.
                    updateAuthAndFirestore(currentUser.uid, newUsername, null) { usernameSuccess ->
                        Log.d(TAG, "saveProfileChanges: updateAuthAndFirestore (username only) callback received. Success: $usernameSuccess")
                        if (usernameSuccess) {
                            if (isPasswordChanged) {
                                Log.d(TAG, "saveProfileChanges: Username update successful. Now updating password.")
                                updatePasswordIfNeeded(newPassword) { passwordSuccess ->
                                    Log.d(TAG, "saveProfileChanges: updatePasswordIfNeeded (after username) callback received. Success: $passwordSuccess")
                                    handleFinalResult(passwordSuccess, "Profile updated, but password change failed.")
                                }
                            } else {
                                Log.i(TAG, "saveProfileChanges: Username update successful. No password change needed.") // Info for success
                                handleFinalResult(true, null) // Username update successful
                            }
                        } else {
                            // Username update failed
                            Log.e(TAG, "saveProfileChanges: updateAuthAndFirestore (username only) FAILED.") // Error level
                            handleFinalResult(false, "Failed to update username.") // Specific error message
                        }
                    }
                } else { // Nothing Changed
                    Log.i(TAG, "saveProfileChanges: No changes detected (Image, Username, Password).") // Info level
                    Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show()
                    setEditingEnabled(true)
                }
            }
        }
    }

    private fun isProfileDataValid(username: String, password: String):Boolean {
        if (username.isEmpty()) {
            usernameEditText.error = "Username cannot be empty"
            return false
        }
        if (username.length < 3 || username.length > 15) {
            usernameEditText.error = "Username must be 3-15 characters long."
            return false
        }
        if (password.isNotEmpty() && password.length < 6) {
            passwordEditText.error = "New password must be at least 6 characters"
            return false
        }
        return true
    }

    private fun checkIfUsernameChanged(userId: String, newUsername: String, callback: (Boolean) -> Unit) {
        Log.d(TAG, "checkIfUsernameChanged: Comparing '$newUsername' for user $userId")
        val currentUser = auth.currentUser ?: return callback(false)
        val currentAuthName = currentUser.displayName

        if (currentAuthName.isNullOrBlank()) {
            Log.d(TAG, "checkIfUsernameChanged: Auth name is blank, checking Firestore.")
            db.collection("users").document(userId).get().addOnCompleteListener { task ->
                var currentDbName = ""
                if (task.isSuccessful && task.result?.exists() == true) {
                    currentDbName = task.result?.getString("username") ?: ""
                    Log.d(TAG, "checkIfUsernameChanged: Firestore name found: '$currentDbName'")
                } else if (!task.isSuccessful) {
                    Log.w(TAG, "checkIfUsernameChanged: Failed to get Firestore name during check.", task.exception)
                } else {
                    Log.w(TAG, "checkIfUsernameChanged: Firestore doc doesn't exist during check.")
                }
                val changed = newUsername != currentDbName
                Log.d(TAG, "checkIfUsernameChanged: Firestore comparison result: $changed")
                callback(changed)
            }
        } else {
            Log.d(TAG, "checkIfUsernameChanged: Comparing with Auth name: '$currentAuthName'")
            val changed = newUsername != currentAuthName
            Log.d(TAG, "checkIfUsernameChanged: Auth comparison result: $changed")
            callback(changed)
        }
    }

    private fun uploadImageToCloudinaryAndUpdateFirebase(
        userId: String,
        newUsername: String,
        imageUri: Uri,
        callback: (Boolean, String?) -> Unit
    ) {
        Log.i(TAG, "uploadImageToCloudinaryAndUpdateFirebase: Starting Cloudinary upload. User: $userId, Preset: $CLOUDINARY_UNSIGNED_UPLOAD_PRESET, Uri: $imageUri") // Info for starting upload
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()

        MediaManager.get().upload(imageUri)
            .unsigned(CLOUDINARY_UNSIGNED_UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d(TAG, "Cloudinary Upload ($requestId): onStart")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val cloudinaryUrl = resultData?.get("secure_url") as? String // Use secure_url
                    Log.i(TAG, "Cloudinary Upload ($requestId): onSuccess. URL: $cloudinaryUrl") // Info for success

                    if (cloudinaryUrl != null) {
                        Log.d(TAG, "Cloudinary Upload ($requestId): Success. Now calling updateAuthAndFirestore...")
                        updateAuthAndFirestore(userId, newUsername, cloudinaryUrl) { success ->
                            Log.d(TAG, "Cloudinary Upload ($requestId): updateAuthAndFirestore callback received. Success: $success")
                            callback(success, cloudinaryUrl) // Report success/failure of Firebase update
                        }
                    } else {
                        Log.e(TAG, "Cloudinary Upload ($requestId): onSuccess BUT secure_url is missing or not a String. ResultData: $resultData") // Error level
                        handleSaveFailure("Image upload error (URL missing).")
                        callback(false, null)
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e(TAG, "Cloudinary Upload ($requestId): onError. Code: ${error?.code}, Description: ${error?.description}") // Error level
                    handleSaveFailure("Image upload failed: ${error?.description}")
                    callback(false, null)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.w(TAG, "Cloudinary Upload ($requestId): onReschedule. Code: ${error?.code}, Description: ${error?.description}") // Warn level
                    handleSaveFailure("Image upload error (rescheduled): ${error?.description}")
                    callback(false, null)
                }
            }).dispatch()
    }

    private fun updateAuthAndFirestore(
        userId: String,
        newUsername: String,
        cloudinaryPhotoUrl: String?,
        callback: (Boolean) -> Unit
    ) {
        Log.i(TAG, "updateAuthAndFirestore: Starting update. User: $userId, Name: '$newUsername', URL: '$cloudinaryPhotoUrl'") // Info level
        val user = auth.currentUser
        if (user == null) {
            Log.e(TAG, "updateAuthAndFirestore: Current user is NULL. Cannot update.")
            return callback(false)
        }

        Log.d(TAG, "updateAuthAndFirestore: Attempting Auth profile update...")
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(newUsername).build()
        val authTask = user.updateProfile(profileUpdates)

        val firestoreUpdateData = mutableMapOf<String, Any>("username" to newUsername)
        if (cloudinaryPhotoUrl != null) {
            firestoreUpdateData["profileImageUrl"] = cloudinaryPhotoUrl
        }
        Log.d(TAG, "updateAuthAndFirestore: Firestore update data prepared: $firestoreUpdateData")
        Log.d(TAG, "updateAuthAndFirestore: Attempting Firestore document update: users/$userId")
        val firestoreTask = db.collection("users").document(userId).update(firestoreUpdateData)

        // --- 3. Coordinate Results ---
        var authSuccess = false
        var firestoreSuccess = false
        var authComplete = false
        var firestoreComplete = false

        fun checkCompletion() {
            if (authComplete && firestoreComplete) {
                val overallSuccess = authSuccess && firestoreSuccess
                Log.i(TAG, "updateAuthAndFirestore: Both tasks complete. AuthSuccess: $authSuccess, FirestoreSuccess: $firestoreSuccess, Overall: $overallSuccess") // Info level
                callback(overallSuccess)
            } else {
                Log.d(TAG, "updateAuthAndFirestore: checkCompletion called. AuthComplete: $authComplete, FirestoreComplete: $firestoreComplete")
            }
        }

        authTask.addOnCompleteListener { task ->
            authSuccess = task.isSuccessful
            authComplete = true
            if (!authSuccess) {
                Log.e(TAG, "updateAuthAndFirestore: Firebase Auth profile update FAILED.", task.exception) // Error level
            } else {
                Log.d(TAG, "updateAuthAndFirestore: Firebase Auth profile update SUCCEEDED.")
            }
            checkCompletion()
        }

        firestoreTask.addOnCompleteListener { task ->
            firestoreSuccess = task.isSuccessful
            firestoreComplete = true
            if (!firestoreSuccess) {
                Log.e(TAG, "updateAuthAndFirestore: Firestore document update FAILED.", task.exception) // Error level
            } else {
                Log.d(TAG, "updateAuthAndFirestore: Firestore document update SUCCEEDED.")
            }
            checkCompletion()
        }
    }

    private fun updatePasswordIfNeeded(newPass: String, callback: (Boolean) -> Unit) {
        if (newPass.isBlank()) {
            Log.d(TAG, "updatePasswordIfNeeded: Password is blank, skipping update.")
            callback(true)
            return
        }

        Log.i(TAG, "updatePasswordIfNeeded: Attempting password update.")
        auth.currentUser?.updatePassword(newPass)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG,"updatePasswordIfNeeded: Password update SUCCEEDED.")
                passwordEditText.text = null
                Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                callback(true)
            } else {
                Log.e(TAG, "updatePasswordIfNeeded: Password update FAILED.", task.exception) // Error level
                val message = task.exception?.message ?: "Unknown error"
                val toastMessage = if (message.contains("RECENT_LOGIN_REQUIRED", ignoreCase = true)) {
                    "Password update failed: Please log out and log back in to change password."
                } else {
                    "Password update failed: $message"
                }
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
                callback(false)
            }
        } ?: run {
            Log.e(TAG, "updatePasswordIfNeeded: Cannot update password, currentUser is null.")
            callback(false)
        }
    }

    private fun handleFinalResult(success: Boolean, failureMessage: String?) {
        Log.d(TAG, "handleFinalResult: Success: $success, Message: '$failureMessage'")
        setEditingEnabled(true)
        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish() // Close the edit activity
        } else {
            Toast.makeText(this, failureMessage ?: "Profile update failed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSaveFailure(message: String) {
        Log.w(TAG, "handleSaveFailure: $message")
        setEditingEnabled(true)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setEditingEnabled(enabled: Boolean) {
        Log.d(TAG, "setEditingEnabled: Setting UI enabled state to $enabled")
        saveChangesButton.isEnabled = enabled
        cancelButton.isEnabled = enabled
        editImageButton.isEnabled = enabled
        usernameEditText.isEnabled = enabled
        passwordEditText.isEnabled = enabled
    }
}