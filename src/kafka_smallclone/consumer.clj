(ns kafka-smallclone.consumer
  (:require [franzy.clients.consumer.client :as consumer]
            [franzy.clients.consumer.protocols :refer :all]
            [franzy.clients.consumer.defaults :as cd]
            [franzy.serialization.deserializers :as deserializers]))

(defn consume! [bootstrap-server topic partition consumer-group duration seek-back consume-callback]
  (let [cc {:bootstrap.servers       bootstrap-server
            :group.id                consumer-group}
        key-deserializer (deserializers/byte-array-deserializer)
        value-deserializer (deserializers/byte-array-deserializer)
        topic-partition {:topic topic :partition partition}
        topic-partitions [{:topic topic :partition partition}]
        options (cd/make-default-consumer-options)
        timer (future (Thread/sleep (* duration 1000)))
        consumed-records (atom 0)]
    (with-open [c (consumer/make-consumer cc key-deserializer value-deserializer options)]
      (assign-partitions! c topic-partitions)
      (seek-to-end-offset! c topic-partitions)
      (seek-to-offset! c topic-partition (- (next-offset c topic-partition) seek-back)) ; rewind
      (while (not (realized? timer))
        (let [cr (poll! c {:poll-timeout-ms 1000})]
          (consume-callback cr)
          (let [count-cr (record-count cr)]
            (if (not (= 0 count-cr))
              (let [max-offset (apply max (map (fn [record] (:offset record)) cr))]
                (seek-to-offset! c topic-partition (+ 1 max-offset))
                (swap! consumed-records + count-cr))))))
      (clear-subscriptions! c))
    @consumed-records))


