# Recovery

1. Reprocess the message again
   * Publish the failed message to a retry topic `DeadLetterPublishingRecoverer` (https://docs.spring.io/spring-kafka/reference/html/#dead-letters)
   * Saved the failed message in a DB  and retry with Scheduler `ConsumerRecordRecoverer`
2. Discard the message
    * Publish the failed record in to DeadLetter Topic  for tracking purpose
    * Saved the failed record into a DB for Tracking Purposes