<p align="center">
<img width="256" height="256" src="images/oewntk.png" alt="OEWNTK">
</p>
<p align="center">
<img width="150" src="images/mavencentral.png" alt="MavenCentral">
</p>

# Open English Wordnet YAML+-to-YAML grinder

This library reads a model from YAML files and writes it to YAML format.

Project [grind_yaml2plus](https://github.com/oewntk/grind_yaml2plus)

See also [model](https://github.com/oewntk/model/blob/master/README.md).

See also [fromyaml](https://github.com/oewntk/fromyaml/blob/master/README.md).

See also [toyaml](https://github.com/oewntk/toyaml/blob/master/README.md).

See also [oewntk](https://github.com/oewntk)
and [globalwordnet/english-wordnet](https://github.com/globalwordnet/english-wordnet).

## Dataflow

![Dataflow](images/dataflow_yaml2plus.png  "Dataflow")

This library reads from the OEWN distribution YAML files and other YAML files that contain extra data.

This output conforms to the **YAML** standards.

## Command line

`grind.sh [YAML] [YAML2] [YAML]`

grinds the YAML database

*where*

[YAML] directory where OEWN distribution YAML files are

[YAML2] directory where extra YAML files are

[YAML] path to output YAML file

## Maven Central

		<groupId>io.github.oewntk</groupId>
		<artifactId>yaml2yaml</artifactId>
		<version>2.3.2</version>

## Dependencies

![Dependencies](images/grind-yaml2plus.png  "Dataflow")
