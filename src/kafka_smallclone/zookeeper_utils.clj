(ns kafka-smallclone.zookeeper-utils
  (:require [franzy.admin.zookeeper.client :as client]
            [franzy.admin.partitions :as partitions]
            [franzy.admin.cluster :as cluster]))


(defn zookeeper [host]
  (client/make-zk-utils {:servers host} false))

(defn zookeeper-close! [zk]
 (.close zk))

(defn partitions-count! [zk topic]
  (count ((keyword topic) (partitions/partitions-for zk [topic]))))

(defn brokers! [zk]
  (cluster/all-brokers zk))

(defn any-broker-host! [zk]
  (str (get-in (first (brokers! zk)) [:endpoints :plaintext :host]) ":" (get-in (first (brokers! zk)) [:endpoints :plaintext :port])))

(defn zookeper-connection-string [host port chroot]
  (str host ":" port (if (not (nil? chroot)) (str "/" chroot))))