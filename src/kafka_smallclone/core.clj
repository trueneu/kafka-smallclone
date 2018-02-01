(ns kafka-smallclone.core
  (:require [kafka-smallclone.cli :as cli]
            [kafka-smallclone.zookeeper-utils :as zk]
            [kafka-smallclone.pipe :as pipe])
  (:gen-class)
  (:import (java.io PrintStream FileOutputStream)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn clone! [options]
  (let [zk-from (zk/zookeeper (:zk-from options))
        zk-to (zk/zookeeper (:zk-to options))
        topic-from (:topic-from options)
        topic-to (:topic-to options)
        duration (:duration options)
        rewind (:rewind options)
        partition-count-from (zk/partitions-count! zk-from topic-from)
        partition-count-to (zk/partitions-count! zk-to topic-to)
        broker-from (zk/any-broker-host! zk-from)
        broker-to (zk/any-broker-host! zk-to)
        consumer-group (:consumer-group options)
        verbose-mode (not (:quiet options))]
    (if (= partition-count-from partition-count-to)
      (let [pipes (doall
                    (map
                      #(future (pipe/pipe! broker-from topic-from broker-to topic-to % consumer-group duration rewind)) (range partition-count-from)))
            records-count (doall (map deref pipes))]
        (map #(zk/zookeeper-close! %) [zk-from zk-to])
        (list 0 (if verbose-mode (str "Total of " (apply + records-count) " records processed") "")))
      (if (some zero? [partition-count-from partition-count-to])
        '(1 "One or more topics don't exist")
        '(2 "Partitions count differ between topics")))))

(defn -main [& args]
  (let [{:keys [options exit-message ok?]} (cli/validate-args args)
        err System/err]
    (System/setErr (PrintStream. (FileOutputStream. "/dev/null")))
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (let [[clone-exit-status clone-exit-message] (clone! options)]
        (exit clone-exit-status clone-exit-message)))))

