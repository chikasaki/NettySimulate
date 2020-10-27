![basic](../../images/netty.jpg)
1. `mixed`包，将`Boss Group`与`Worker Group`中线程的任务结合起来了
2. `separate`和`strongseparate`都对`Boss Group`和`Worker Group`
做了解耦，其中`separate`和`mixed`比较像；而`strongseparate`进行了
强解耦，提升了一些可扩展性