(ns kafka-smallclone.cli
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as string]))

(def cli-options
  [["-f" "--zk-from HOST" "Zookeeper source"]
   ["-t" "--zk-to HOST" "Zookeeper target"]
   ["-i" "--topic-from TOPIC" "Source topic"]
   ["-o" "--topic-to TOPIC" "Target topic"]
   ["-r" "--rewind AMOUNT" "Amount of messages to rewind back from latest"
    :parse-fn #(Integer/parseInt %)
    :default 100]
   ["-w" "--duration SECONDS" "For how long should smallclone work"
    :parse-fn #(Integer/parseInt %)
    :default 5]
   ["-c" "--consumer-group GROUP" "Consumer group name to use"
    :default "smallclone"]
   ["-q" "--quiet" "Quiet mode"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["kafka-smallclone is an ad-hoc utility to reproduce data from one kafka topic to another."
        ""
        "Usage: kafka-smallclone --zk-from host:port[/chroot] --zk-to host:port[/chroot] --topic-from topic --topic-to topic [options]"
        ""
        "Please note that number of partitions in -from and -to topic must be the same."
        ""
        "Options:"
        options-summary]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors) "\nPlease use --help to print usage"))

(def required-options
  [:zk-from :zk-to :topic-from :topic-to])

(def required-options-error-msg
  [(str "One or more required options missing: " (string/join ", " required-options))])

(defn validate-args [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}
      errors
      {:exit-message (error-msg errors)}
      (not (every? true? (map #(contains? options %) required-options)))
      {:exit-message (error-msg required-options-error-msg)}
      :else
      {:options options})))
