package api

type Response struct {
	Status int                 `json:"status"`
	Errors map[string][]string `json:"errors"`
}

func Error(msgs map[string][]string, statusCode int) Response {
	return Response{
		Status: statusCode,
		Errors: msgs,
	}
}
