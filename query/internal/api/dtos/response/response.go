package response

// Response is a generic structure used for sending API responses.
// It includes a Success field to indicate the status of the operation
// and a Data field of generic type T to contain the response data.
// The Data field will be omitted from JSON output if it's empty.
type Response[T any] struct {
	Success bool `json:"success"`
	Data    T    `json:"data,omitempty"`
}

// NewResponse creates a new success response with the provided data.
// It accepts a generic parameter T for the data type and returns a pointer to a Response struct.
// The Success field in the response is set to true, and Data field is set to the provided data.
func NewResponse[T any](data T) *Response[T] {
	return &Response[T]{
		Success: true,
		Data:    data,
	}
}

// ErrorResponse represents the structure of an error response sent to clients.
// It includes a success flag (typically false for errors), the error type,
// a human-readable error message, and optional additional details about the error.
type ErrorResponse struct {
	Success bool           `json:"success"`
	Error   string         `json:"error"`
	Message string         `json:"message"`
	Details map[string]any `json:"details,omitempty"`
}

// NewErrorResponse creates a new ErrorResponse instance with specified error details.
// It sets the Success field to false and populates the Error, Message, and Details fields.
//
// Parameters:
//   - errorCode: A string representing the error code.
//   - message: A string containing the error message.
//   - details: A map with additional error context information.
//
// Returns:
//   - *ErrorResponse: A pointer to the created ErrorResponse.
func NewErrorResponse(errorCode string, message string, details map[string]interface{}) *ErrorResponse {
	return &ErrorResponse{
		Success: false,
		Error:   errorCode,
		Message: message,
		Details: details,
	}
}

// ValidationErrorResponse represents a response structure for validation errors in API responses.
// It contains a success status flag and a map of field validation errors.
type ValidationErrorResponse struct {
	Success bool              `json:"success"`
	Errors  map[string]string `json:"errors"`
}

// NewValidationErrorResponse creates a new ValidationErrorResponse with provided validation errors.
// It sets the Success field to false and populates the Errors field with the given map of error messages.
// The errors map should contain field names as keys and corresponding error messages as values.
//
// Parameters:
//   - errors: A map of field names to error messages representing validation failures
//
// Returns:
//   - *ValidationErrorResponse: A pointer to the created ValidationErrorResponse
func NewValidationErrorResponse(errors map[string]string) *ValidationErrorResponse {

	return &ValidationErrorResponse{
		Success: false,
		Errors:  errors,
	}
}
