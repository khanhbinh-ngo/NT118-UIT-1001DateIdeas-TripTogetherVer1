package com.example.triptogether.data

import android.net.Uri
import com.example.triptogether.model.Trip
import com.example.triptogether.utilities.Constants
import com.example.triptogether.utilities.LocationUtils

class CreateTripRepository : CreateTripDataSource {
    override suspend fun create(
        name: String,
        country: String,
        start: String,
        end: String,
        emails: List<String>,
        photo: Uri?,
    ) {
        val uid = AuthRepository().requireUid()
        val members = mutableSetOf(uid)
        for (email in emails) {
            val member =
                UserRepository().findUid(email) ?: throw UnregisteredTravelerException(email)
            members.add(member)
        }
        val repository = TripRepository()
        val id = repository.newId()
        val coordinates =
            LocationUtils.getCoordinatesFromLocationName(country, Constants.GEOCODING_API_KEY)
        val image =
            photo?.let {
                val mime =
                    com.example.triptogether.drive.DriveSession.context.contentResolver
                        .getType(it)
                        .orEmpty()
                require(mime.startsWith("image/"))
                com.example.triptogether.drive.DriveApi().upload(it, mime).url
            }
        repository.save(
            Trip(
                id = id,
                name = name,
                country = country,
                startDate = start,
                endDate = end,
                members = members.toList(),
                createdBy = uid,
                latitude = coordinates?.first ?: 0.0,
                longitude = coordinates?.second ?: 0.0,
                imageUrl = image,
            )
        )
    }
}

class UnregisteredTravelerException(val email: String) : IllegalArgumentException()
