(ns kafka-smallclone.producer
  (:require [franzy.serialization.serializers :as serializers]
            [franzy.clients.producer.client :as producer]
            [franzy.clients.producer.defaults :as pd]
            [franzy.clients.producer.protocols :refer :all]
            [clojure.string :as str]))

(defn make-producer [bootstrap-server]
  (let [pc {:bootstrap.servers bootstrap-server
            :acks              "all"
            :retries           0
            :batch.size        16384
            :linger.ms         10
            :buffer.memory     33554432}
        key-serializer (serializers/byte-array-serializer)
        value-serializer (serializers/byte-array-serializer)
        options (pd/make-default-producer-options)]
    (producer/make-producer pc key-serializer value-serializer options)))

(defn produce! [producer topic partition key value opts]
  (send-async! producer topic partition key value opts))