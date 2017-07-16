# kafka-smallclone

An ad-hoc utility to pump your data between kafka topics. 

## Usage

    kafka-smallclone is an ad-hoc utility to reproduce data from one kafka topic to another.
    Usage: kafka-smallclone --zk-from host:port/chroot --zk-to host:port/chroot --topic-from topic --topic-to topic [options]
    Please note that number of partitions in -from and -to topic must be the same.
    Options:
      -f, --zk-from HOST                      Zookeeper source
      -t, --zk-to HOST                        Zookeeper target
      -i, --topic-from TOPIC                  Source topic
      -o, --topic-to TOPIC                    Target topic
      -r, --rewind AMOUNT         100         Amount of messages to rewind back from latest
      -w, --duration SECONDS      5           For how long should smallclone work
      -c, --consumer-group GROUP  smallclone  Consumer group name to use
      -q, --quiet                             Quiet mode
      -h, --help
      
A common usecase for this would be if you want to debug
your application using production data, but your dev environment
cluster is too small to hold all the production data.
      
## Examples

    ./kafka-smallclone -f zookeeper1.company1.com/cluster1 -t zookeeper2.company2.com/cluster2 -i production_topic -o debug_topic -r 100 -w 10 

This will rewind 100 messages from the latest one in the production_topic in cluster1,
consume them and then produce them back to debug_topic in cluster2. kafka-smallclone will
run for no more than 10 seconds, and if something happens to be produced while
it's running, it will also be consumed and re-produced.

## Building

Install leiningen (https://leiningen.org/)

Install lein-binplus (https://github.com/BrunoBonacci/lein-binplus) 
To do so, add the following to your `~/.lein/profiles.clj`'s `{:user {:plugins []}}` list: `[lein-binplus "0.6.2"]`.

Build the binary:
`lein with-profile common bin`

Enjoy!

## License

Copyright © 2017 Pavel Gurkov

Distributed under the Eclipse Public License either version 1.0 or any later version.