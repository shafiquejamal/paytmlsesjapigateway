package access

import java.util.UUID

import play.api.mvc.{Request, WrappedRequest}

class AuthenticatedRequest[A](val userId: UUID, val username: String, request: Request[A]) extends WrappedRequest[A](request)
