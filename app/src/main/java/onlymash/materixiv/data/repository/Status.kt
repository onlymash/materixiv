package onlymash.materixiv.data.repository


enum class Status {
    /**
     * There is current a running request.
     */
    RUNNING,

    /**
     * The last request has succeeded or no such requests have ever been run.
     */
    SUCCESS,

    REFRESH_TOKEN,

    /**
     * The last request has failed.
     */
    FAILED
}