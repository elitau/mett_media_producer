create Table if not exists mediaobject (
                           id INTEGER PRIMARY KEY AUTO_INCREMENT,
                           title Varchar(80),
                           outline Text,
                           duration Time,
                           release_date Date);
                          
create Table if not exists codec (
                           txt Varchar(10) Primary Key);
                           
create Table if not exists structure (
                           txt Varchar(10) Primary Key);
                           
create Table if not exists videoformat (
                           id varchar(50) Primary Key,
                           codec_id varchar(10),
                           struc_id varchar(10),
                           res_lines Integer,
                           framerate Integer,
                           bitrate Integer,
                           formatName varchar(50)
                          );
                           
create Table if not exists mediainstance (
                           id INTEGER PRIMARY KEY AUTO_INCREMENT,
                           moid Integer,
                           vid_format varchar(50),
                           res_horiz Integer,
                           frames Integer,
                           location Varchar(60),
                           duration Time,
                           source bool);
                           
create Table if not exists setting (
                           id INTEGER PRIMARY KEY AUTO_INCREMENT,
                           prefix Integer not null,
                           name Varchar(60) not null,
                           value BLOB,
                           domain BLOB,
                           description Text) Type=InnoDB;
                           
create Table if not exists componentprefix (
                           classname Varchar(100) PRIMARY KEY,
                           prefix Integer NOT NULL);
                           
create Table if not exists settingProxy (
                           prefix smallint primary key not null,
                           JNDIname varchar(100) not null);
                           
create Table if not exists mmUser (
                           username varchar(20) primary key not null,
                           passwd varchar(10) not null
                           );
                           
create Table if not exists plugin (
                           pluginID INTEGER PRIMARY KEY AUTO_INCREMENT,
                           classname varchar(200) not null
                           );
                           
create Table if not exists plugin_proxy (
                           pluginID INTEGER PRIMARY KEY,
                           isInput bit,
                           protocolID varchar(50)
                           );
                           
create Table if not exists plugin_converter (
                           pluginID INTEGER,
                           from_codec varchar(10),
                           from_structure varchar(10),
                           to_format varchar(50),
                           costs Integer
                           );
                           
create Table if not exists protocol (
                           protocolID varchar(50) Primary Key,
                           longName varchar(100)
                           );
                           
create Table if not exists plugin_transporter (
                           pluginID INTEGER PRIMARY KEY,
                           isInput bit,
                           protocolID varchar(50)
                           );
