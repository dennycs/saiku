/*
 *   Copyright 2012 OSBI Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.saiku.repository;

import net.thucydides.core.annotations.Step;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;


/**
 * Created by bugg on 14/05/14.
 */
public class JackrabbitSteps {

  private IRepositoryManager iRepositoryManager = JackRabbitRepositoryManager.getJackRabbitRepositoryManager(null,
      null);


  @Step
  public void initializeRepository() {
    iRepositoryManager.init();
  }

  @Step
  public boolean startRepository() {
    try {
      return iRepositoryManager.start(null);
    } catch ( RepositoryException e ) {
      e.printStackTrace();
    }

    return false;
  }

  @Step
  public void initializeUsers(List<String> users) {

    for(String u : users){
      try {
        iRepositoryManager.createUser(u);
      } catch ( RepositoryException e ) {
        e.printStackTrace();
      }
    }
  }

  public List<String> getHomeDirectoryList() {
    NodeIterator nodes = null;
    try {
      nodes = iRepositoryManager.getHomeFolders();
    } catch ( RepositoryException e ) {
      e.printStackTrace();
    }

    List<String> names = new ArrayList<String>();
    while(nodes.hasNext()){
      Node node = nodes.nextNode();
      try {
        names.add(node.getProperty( "user" ).getString());
      } catch ( RepositoryException e ) {
        e.printStackTrace();
      }
    }

    return names;

  }


  public Node getHomeDirectory( String directory ) {
    Node node = null;
    try {
      node = iRepositoryManager.getHomeFolder( directory );
        String path = node.getPath();
    } catch ( RepositoryException e ) {
      e.printStackTrace();
    }

    return node;
  }

  @Step
  public void shutdownRepository() {
      JackRabbitRepositoryManager.getJackRabbitRepositoryManager(null,null).shutdown();
  }

  @Step
  @Test( expected = RepositoryException.class )
  public void initializeDuplicateUsers( List<String> users ) throws RepositoryException {
    for(String u : users){
        iRepositoryManager.createUser(u);
    }
  }

  @Step
  public boolean createFolder( String username, String folder ) throws RepositoryException {
    return iRepositoryManager.createFolder(username, folder);

  }

  @Step
  public boolean deleteFolder( String username, String folder ) throws RepositoryException {
    return iRepositoryManager.deleteFolder(folder);
  }

  @Step
  @Test( expected = PathNotFoundException.class)
  public void getBrokenFolders(String user, String folder) throws RepositoryException {
    iRepositoryManager.getFolder(user, folder);
  }

    @Step
    public Node getFolders(String user, String folder) throws RepositoryException {
        return iRepositoryManager.getFolder(user, folder);
    }

  @Step
  public void deleteAllNodes() throws RepositoryException {
    iRepositoryManager.deleteRepository();
  }

    @Step
    public void moveFolder(String user, String folder, String source, String target) throws RepositoryException {
        if(target.equals("home")){
            target = null;
        }
        iRepositoryManager.moveFolder(user, folder, source, target);
    }

    @Step
    public void saveFile(Object file, String path, String user, String type) throws RepositoryException {
        iRepositoryManager.saveFile(file, path, user, type, null);
    }

    @Step
    public String getFile(String s, String username) throws RepositoryException {
        return iRepositoryManager.getFile(s, username, null);
    }

    @Step
    public byte[] getBackup() throws IOException, RepositoryException {
        return iRepositoryManager.exportRepository();
    }
}

