# s71-challenge

The challenge objective is to create a FiFo multi-queue with messages stored in a MySQL database, with an implementation that is independent of test data or use case.

## Installation

From the root directory on your application, run the resources/schema.sql script to create the table in your database.

```bash
$ mysql --host=hostname --user=username --database=dbname
```
mysql prompt will ask for your password. Enter your password and run:

```mysql
mysql> \. resources/schema.sql
```
*** 
Make shure you have a config var `JDBC_DATABASE_URL` with your database server and credentials, following to this pattern:

`jdbc:mysql://server-address:port/dbname?password=your-password&user=your-user`

Example:

`jdbc:mysql://127.0.0.0:3306/mydatabase?password=1234&user=johnnymarr`

## Usage

To run the app 

    $ lein repl

From the repl type `(user/reset)` and press `enter`

Among other messages you shold see something like:
 
```bash
 Configured db

 Started app

 Server running on port 5000
 
 :resumed
```
The application should be running on http://localhost:5000

## Notes

I chose to expose endpoints of the stubbed functions and deploy the app on heroku

https://omendoza-s71challenge.herokuapp.com


## Tests

I'm testing the endpoints directly in a single file. To run the tests:

* On spacemacs:
Run project tests `(C-c C-t C-p)`

* On IntelliJ Cursive, open the queue_tests.clj file and call actions:
`Run Tests in Current NS in REPL`

* On VS Code 
from command bar: `>Calva:Run All Tests `
...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
