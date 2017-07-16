(ns kafka-smallclone.pipe
  (:require [kafka-smallclone.consumer :as consumer]
            [kafka-smallclone.producer :as producer]))

(defn pipe! [bootstrap-consume-server consume-topic bootstrap-produce-server produce-topic
             partition consumer-group duration seek-back]
  (let [producer (producer/make-producer bootstrap-produce-server)]
    (consumer/consume! bootstrap-consume-server consume-topic partition consumer-group duration seek-back
                       (fn [records]
                         (doseq [record records]
                           (producer/produce! producer produce-topic partition
                                                  (:key record) (:value record) nil))))))