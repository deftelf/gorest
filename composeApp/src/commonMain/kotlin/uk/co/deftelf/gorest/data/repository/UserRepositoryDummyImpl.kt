package uk.co.deftelf.gorest.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.co.deftelf.gorest.data.local.GoRestDatabase
import uk.co.deftelf.gorest.data.mapper.UserMapper.toDomain
import uk.co.deftelf.gorest.data.remote.GoRestApiService
import uk.co.deftelf.gorest.data.remote.dto.UserDto
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.model.UserStatus
import uk.co.deftelf.gorest.domain.repository.UserRepository
import uk.co.deftelf.gorest.util.AuthTokenProvider
import kotlin.time.Clock

class UserRepositoryDummyImpl() : UserRepository {
    override fun observeUsers(): Flow<List<User>> =
        flow {
            delay(2000)
            emit(listOf(
                User(
                    1,
                    "Billy bob",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    2,
                    "Billy bob2",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    3,
                    "Billy bob3",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    4,
                    "Billy bob4",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    5,
                    "Billy bob5",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    6,
                    "Billy bob6",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    7,
                    "Billy bob7",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    8,
                    "Billy bob8",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    9,
                    "Billy bob9",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    10,
                    "Billy bob10",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    11,
                    "Billy bob",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    12,
                    "Billy bob",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    13,
                    "Billy bob",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    14,
                    "Billy bob",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                ),
                User(
                    15,
                    "Billy bob",
                    "blah@example.com",
                    Gender.male,
                    status = UserStatus.active,
                    createdAt = Clock.System.now()
                )
            ))
        }

    override suspend fun refreshLastPage(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun createUser(
        name: String,
        email: String,
        gender: String,
        status: String
    ): Result<User> {
        delay(1000)
        return Result.failure(Exception("failed to creaete user"))
    }

    override suspend fun deleteUser(id: Long): Result<Unit> {
        delay(1000)
        return Result.failure(Exception("failed to delete user"))
    }
}
