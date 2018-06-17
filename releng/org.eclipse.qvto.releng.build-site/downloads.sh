#!/bin/bash -xv
#*******************************************************************************
# Copyright (c) 2017, 2018 Willink Transformations and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v20.html
#
# Contributors:
#     E.D.Willink - initial API and implementation
#*******************************************************************************
#
#	Promote the PUBLISH__URL to the downloads 'page'.
#
#    PUBLISH__URL            The zip to be published e.g. https://ci.eclipse.org/qvt-oml/job/qvto-master/25/artifact/releng/org.eclipse.qvto.releng.build-site/target/org.eclipse.qvto-3.9.0.v20171025-1600.zip
#    PUBLISH__VERSION        Unqualified version e.g. 3.9.0
#    PUBLISH__BUILD_T        Build type N/I/S, blank suppresses promotion
#    PUBLISH__QUALIFIER      Version qualifier e.g. v20171025-1600
#    PUBLISH__ALIAS          Non blank to use alias as part of final name
#    PUBLISH__JAVADOC        The optional Javadoc zip to be published e.g. https://ci.eclipse.org/qvt-oml/job/qvto-master/25/artifact/releng/org.eclipse.qvto.releng.build-site/target/QVTo-javadoc.zip
#
dropsFolder="/home/data/httpd/download.eclipse.org/mmt/qvto/downloads/drops/"
javadocFolder="/home/data/httpd/download.eclipse.org/mmt/qvto/javadoc/m2m.qvt.oml/"
group="modeling.mmt.qvt-oml"
zipPrefix="mmt-qvto-Update-"
localZip="newJavadoc.zip"

if [ -n "${PUBLISH__BUILD_T}" ]
then

  tQualifier="${PUBLISH__BUILD_T}${PUBLISH__QUALIFIER:1:8}${PUBLISH__QUALIFIER:10:4}"
  versionFolder="${dropsFolder}${PUBLISH__VERSION}/${tQualifier}"
  if [ ! -d "${versionFolder}" ]
  then
    mkdir -p ${versionFolder}
  fi

  fileStem="${tQualifier}"
  if [ -n "${PUBLISH__ALIAS}" ]
  then
    fileStem=${PUBLISH__ALIAS}
  fi
  zipFile="${zipPrefix}${fileStem}.zip"

  pushd ${versionFolder}
    curl -s -k ${PUBLISH__URL} > ${zipFile}
    md5sum -b ${zipFile} > ${zipFile}.md5
    sha512sum -b ${zipFile} > ${zipFile}.sha1
    # make sure permissions are for the intended group
    chgrp -R ${group} ${zipFile} ${zipFile}.md5 ${zipFile}.sha1
    chmod -R g+w ${zipFile} ${zipFile}.md5 ${zipFile}.sha1
  popd
  
  if [ ! -d "${javadocFolder}" ]
  then
    mkdir ${javadocFolder}
  fi
  pushd ${javadocFolder}
    curl -s -k ${PUBLISH__JAVADOC} > ${localZip}
    if [ $? -eq 0 ]
    then
      javadocSize=$(wc -c <"$localZip")
      if [ ${javadocSize} -ge 100000 ]						# A small (423 byte) file is an HTTP 404 message
      then
        unzip -ou ${localZip} -d new${PUBLISH__VERSION}
        chgrp -R ${group} new${PUBLISH__VERSION}
        chmod -R g+w new${PUBLISH__VERSION}
        rm -rf ${localZip}
        if [ -d "${PUBLISH__VERSION}" ]
        then
          mv ${PUBLISH__VERSION} old${PUBLISH__VERSION}
          mv new${PUBLISH__VERSION} ${PUBLISH__VERSION}
          rm -rf old${PUBLISH__VERSION}
        else
          mv new${PUBLISH__VERSION} ${PUBLISH__VERSION}
        fi
      fi
    fi
  popd

fi