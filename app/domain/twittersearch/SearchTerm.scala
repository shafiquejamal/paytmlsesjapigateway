package domain.twittersearch

import java.util.UUID

import org.joda.time.DateTime

case class SearchTerm(userId: UUID, searchText: String, createdAt: DateTime)
