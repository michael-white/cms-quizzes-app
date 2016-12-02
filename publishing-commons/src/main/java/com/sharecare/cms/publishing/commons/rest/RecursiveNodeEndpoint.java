/**
 * This file Copyright (c) 2012-2016 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package com.sharecare.cms.publishing.commons.rest;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import info.magnolia.cms.util.ExclusiveWrite;
import info.magnolia.cms.util.PathUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.rest.AbstractEndpoint;
import info.magnolia.rest.service.node.v1.RepositoryMarshaller;
import info.magnolia.rest.service.node.v1.RepositoryNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Endpoint for accessing and manipulating nodes.
 *
 * @param <D> The endpoint definition
 */
@Api(value = "/nodesrec/v1.1", description = "The nodes API 1.1")
@Path("/nodes/v1.1")
public class RecursiveNodeEndpoint<D extends RecursiveNodeEndpointDefinition> extends AbstractEndpoint<D> {

    private static final String STATUS_MESSAGE_OK = "OK";
    private static final String STATUS_MESSAGE_BAD_REQUEST = "Request not understood due to errors or malformed syntax";
    private static final String STATUS_MESSAGE_UNAUTHORIZED = "Unauthorized";
    private static final String STATUS_MESSAGE_ACCESS_DENIED = "Access denied";
    private static final String STATUS_MESSAGE_NODE_NOT_FOUND = "Node not found";
    private static final String STATUS_MESSAGE_ERROR_OCCURRED = "Error occurred";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RepositoryMarshaller marshaller = new RepositoryMarshaller();

    @Inject
    public RecursiveNodeEndpoint(final D endpointDefinition) {
        super(endpointDefinition);
    }

    /**
     * Returns a node including its properties and child nodes down to a certain depth.
     */
    @GET
    @Path("/{workspace}{path:(/.+)?}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a node", notes = "Returns a node from the specified workspace and path")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK, response = RepositoryNode.class),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response readNode(
            @PathParam("workspace") String workspaceName,
            @PathParam("path") @DefaultValue("/") String path,
            @QueryParam("depth") @DefaultValue("0") int depth,
            @QueryParam("excludeNodeTypes") @DefaultValue("") String excludeNodeTypes,
            @QueryParam("includeMetadata") @DefaultValue("false") boolean includeMetadata) throws RepositoryException {

        final String absPath = StringUtils.defaultIfEmpty(path, "/");

        final Session session = MgnlContext.getJCRSession(workspaceName);

        if (!session.nodeExists(absPath)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Node node = session.getNode(absPath);

        final RepositoryNode response = marshaller.marshallNode(node, depth, splitExcludeNodeTypesString(excludeNodeTypes), includeMetadata);

        log.debug("Returned node [{}]", node.getPath());

        return Response.ok(response).build();
    }

    /**
     * Creates a new node and populates it with the supplied properties. Does not support adding sub nodes. The
     * submitted node must contain name and type.
     */
    @PUT
    @Path("/{workspace}{path:(/.+)?}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Create a node", notes = "Creates a node and adds passed properties")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_BAD_REQUEST),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 403, message = STATUS_MESSAGE_ACCESS_DENIED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response createNode(
            @PathParam("workspace") String workspaceName,
            @PathParam("path") @DefaultValue("/") String parentPath,
            RepositoryNode repositoryNode) throws RepositoryException {

        final String parentAbsPath = StringUtils.defaultIfEmpty(parentPath, "/");

        if (StringUtils.isEmpty(repositoryNode.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (StringUtils.isEmpty(repositoryNode.getType())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!StringUtils.isEmpty(repositoryNode.getPath()) && !repositoryNode.getPath().equals(PathUtil.createPath(parentAbsPath, repositoryNode.getName()))) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Session session = MgnlContext.getJCRSession(workspaceName);


        if (!session.nodeExists(parentAbsPath)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Node parentNode = session.getNode(parentAbsPath);


        if (parentNode.hasNode(repositoryNode.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        final Node node = assembleNode(repositoryNode, parentNode);

        synchronized (ExclusiveWrite.getInstance()) {
            session.save();
        }

        log.debug("Created a new node [{}]", node.getPath());

        return Response.ok().build();
    }

    private void assembleChildren(RepositoryNode repositoryNode, Node node) throws RepositoryException {
        if (repositoryNode.getNodes() != null && !repositoryNode.getNodes().isEmpty()) {

            for(RepositoryNode r : repositoryNode.getNodes())
                assembleNode(r, node);
        }
    }

    private Node assembleNode(RepositoryNode repositoryNode, Node parentNode) throws RepositoryException {
        final Node node = parentNode.addNode(repositoryNode.getName(), repositoryNode.getType());

        if (repositoryNode.getProperties() != null) {
            marshaller.unmarshallProperties(node, repositoryNode.getProperties());
        }
        assembleChildren(repositoryNode, node);
        return node;
    }

    /**
     * Adds properties to a node. Existing properties are changed if present in the request. Existing properties not
     * present in the request are not removed.
     */
    @POST
    @Path("/{workspace}{path:(/.+)?}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Update a node", notes = "Updates a node by adding passed properties")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_BAD_REQUEST),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 403, message = STATUS_MESSAGE_ACCESS_DENIED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response updateNode(
            @PathParam("workspace") String workspaceName,
            @PathParam("path") @DefaultValue("/") String path,
            RepositoryNode repositoryNode) throws RepositoryException {

        final String absPath = StringUtils.defaultIfEmpty(path, "/");

        if (repositoryNode.getPath() != null && !StringUtils.equals(absPath, repositoryNode.getPath())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String name = StringUtils.substringAfterLast(absPath, "/");
        if (repositoryNode.getName() != null && !StringUtils.equals(name, repositoryNode.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (repositoryNode.getNodes() != null && !repositoryNode.getNodes().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Session session = MgnlContext.getJCRSession(workspaceName);

        if (!session.nodeExists(absPath)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Node node = session.getNode(absPath);

        if (repositoryNode.getType() != null && !repositoryNode.getType().equals(node.getPrimaryNodeType().getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (repositoryNode.getIdentifier() != null && !repositoryNode.getIdentifier().equals(node.getIdentifier())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (repositoryNode.getProperties() != null) {
            marshaller.unmarshallProperties(node, repositoryNode.getProperties());
        }

        synchronized (ExclusiveWrite.getInstance()) {
            session.save();
        }

        log.debug("Updated node [{}]", node.getPath());

        return Response.ok().build();
    }

    /**
     * Delete a node.
     */
    @DELETE
    @Path("/{workspace}{path:(/.+)?}")
    @ApiOperation(value = "Delete a node", notes = "Deletes a node")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 403, message = STATUS_MESSAGE_ACCESS_DENIED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response deleteNode(
            @PathParam("workspace") String workspaceName,
            @PathParam("path") @DefaultValue("/") String path) throws RepositoryException {

        final String absPath = StringUtils.defaultIfEmpty(path, "/");

        final Session session = MgnlContext.getJCRSession(workspaceName);

        if (!session.nodeExists(absPath)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Node node = session.getNode(absPath);

        node.remove();

        synchronized (ExclusiveWrite.getInstance()) {
            session.save();
        }

        log.debug("Deleted node [{}]", absPath);

        return Response.ok().build();
    }

    protected List<String> splitExcludeNodeTypesString(String excludes) {
        List<String> excludeList = new ArrayList<String>();

        if (excludes != null) {
            excludes = StringUtils.replace(excludes, " ", "");
            excludeList = Arrays.asList(StringUtils.split(excludes, ","));
        }

        return excludeList;
    }
}
