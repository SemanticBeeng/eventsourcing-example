Study in functional domain model design with Scala
----
 Derived directly from work of Martin Krasser
<table>
    <tr>
        <td><b>Part</b></td>
        <td><b>Branch</b></td>
    </tr>
    <tr>
        <td><a href="http://krasserm.blogspot.com/2011/11/building-event-sourced-web-application.html">Building an Event-Sourced Web Application - Part 1: Domain Model, Events and State</a></td>
        <td><a href="https://github.com/krasserm/eventsourcing-example/tree/part-1">part-1</a></td>
    </tr>
    <tr>
        <td><a href="http://krasserm.blogspot.com/2012/01/building-event-sourced-web-application.html">Building an Event-Sourced Web Application - Part 2: Projections, Persistence, Consumers and Web Interface</a> </td>
        <td><a href="https://github.com/krasserm/eventsourcing-example/tree/part-2">part-2</a></td>
    </tr>
</table>

Changes
---

* Removed XML bindings; will use Play-JSON (less intrusive)
* Will persist to a relational DB with every logical transaction and "rehydrate" when needed
 