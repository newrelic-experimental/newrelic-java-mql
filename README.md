<a href="https://opensource.newrelic.com/oss-category/#new-relic-experimental"><picture><source media="(prefers-color-scheme: dark)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/dark/Experimental.png"><source media="(prefers-color-scheme: light)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"><img alt="New Relic Open Source experimental project banner." src="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"></picture></a>


![GitHub forks](https://img.shields.io/github/forks/newrelic-experimental/newrelic-java-mql?style=social)
![GitHub stars](https://img.shields.io/github/stars/newrelic-experimental/newrelic-java-mql?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/newrelic-experimental/newrelic-java-mql?style=social)

![GitHub all releases](https://img.shields.io/github/downloads/newrelic-experimental/newrelic-java-mql/total)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/newrelic-experimental/newrelic-java-mql)
![GitHub last commit](https://img.shields.io/github/last-commit/newrelic-experimental/newrelic-java-mql)
![GitHub Release Date](https://img.shields.io/github/release-date/newrelic-experimental/newrelic-java-mql)


![GitHub issues](https://img.shields.io/github/issues/newrelic-experimental/newrelic-java-mql)
![GitHub issues closed](https://img.shields.io/github/issues-closed/newrelic-experimental/newrelic-java-mql)
![GitHub pull requests](https://img.shields.io/github/issues-pr/newrelic-experimental/newrelic-java-mql)
![GitHub pull requests closed](https://img.shields.io/github/issues-pr-closed/newrelic-experimental/newrelic-java-mql)


# New Relic Java Instrumentation for Enovia MQL Queries

Provides instrumentation for capturing Enovia MQL queries.  It captures the MQL query and reports it as a Database call.   


## Installation

This use this instrumentation.   
1. Download the latest release.    
2. In the New Relic Java Agent directory (directory containing newrelic.jar), create a directory named extensions if it doe not already exist.   
3. Copy the release jars into the extensions directory.
4. Restart the application.


## Getting Started

After deployment, you should be able to see MQL queries showing up in transaction traces.

## Query Reporting   
   
An MQL query is constructed from an MQL Command which is comprised of a query and a set of string parameters.   The query will contain sequential $n starting with $1 and will contain the same number of arguments as there are in the set of string parameters.   Database queries in New Relic can be set to be recorded via the newrelic.yml configuration file.   It has a record_sql attribute that can be set to one of these three values: off, raw or obfuscated.   If set to obfuscated, then the Span recording the MQL query will set the db.statement attribute to the MQL command.  If set to raw, then the db.statement attribute will be set to a string where $n in the command is replaced by the corresponding string parameter.  If set to off, then db.statement is not reported.  

Additional there are two additonal attributes added to the Span, MQL-Command and MQL-Command-WithArgs.   The values in these attributes depends of the settings.   
   
Note that the report_sql setting applies to all database queries not just to the MQL queries.  Hence if set to raw then all database spans will be reported as raw. If you are using other databases with your application and do not want them to be reported as raw, then additional settings are available that only apply to the MQL queries.   
   
The settings are set in newrelic.yml.  If not present then default values are used.    
   
The setting are included in a MQL.Reporting stanza (sample shown below).   It includes two settings: enabled and type.  The enabled setting is given a true or false value.  The type setting takes the same values as report_sql (off, raw or obfuscated).   Default value for enabled is false and default value for obfuscated.   
   
### Example
  MQL:
    Reporting:
      enabled: true
      type: obfuscated
   
### Behavior of Settings
#### record_sql is raw
MQL settings are ignored.   
MQL-Command is set to the command and MQL-Command-WithArgs is set to the command with parameters populated.   
#### record_sql is obfuscated, MQL.Reporting.enabled is false  
MQL-Command is set to the command and MQL-Command-WithArgs is set to "Unable to record".   
#### record_sql is off, MQL.Reporting.enabled is false  
MQL-Command and MQL-Command-WithArgs are set to "Unable to record".   
#### record_sql is obfuscated or off, MQL.Reporting.enabled is true  (override)
##### MQL.Reporting.type is off   
MQL-Command and MQL-Command-WithArgs are set to "Unable to record".   
##### MQL.Reporting.type is obfuscated   
MQL-Command is set to the command and MQL-Command-WithArgs is set to "Unable to record".   
##### MQL.Reporting.type is raw   
MQL-Command is set to the command and MQL-Command-WithArgs is set to the command with parameters populated.   

## Building

If you make changes to the instrumentation code and need to build the instrumentation jars, follow these steps
1. Set environment variable NEW_RELIC_EXTENSIONS_DIR.  Its value should be the directory where you want to build the jars (i.e. the extensions directory of the Java Agent).   
2. Using open JDK 8 build one or all of the jars using .   
a. To build one jar, run the command:  gradlew _moduleName_:clean  _moduleName_:install    
b. To build all jars, run the command: gradlew clean install
3. Restart the application

## Support

New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

>We encourage you to bring your experiences and questions to the [Explorers Hub](https://discuss.newrelic.com) where our community members collaborate on solutions and new ideas.

## Contributing

We encourage your contributions to improve Salesforce Commerce Cloud for New Relic Browser! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project. If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company, please drop us an email at opensource@newrelic.com.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).

## License

New Relic Java Instrumentation for  Adobe Granite is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.
