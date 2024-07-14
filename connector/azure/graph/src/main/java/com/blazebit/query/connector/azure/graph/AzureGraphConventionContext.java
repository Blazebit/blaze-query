/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.connector.azure.graph;

import java.lang.reflect.Method;

import com.blazebit.query.connector.base.ConventionContext;
import com.microsoft.graph.models.ActivityHistoryItem;
import com.microsoft.graph.models.BaseItem;
import com.microsoft.graph.models.ChatMessage;
import com.microsoft.graph.models.ColumnDefinition;
import com.microsoft.graph.models.ContactFolder;
import com.microsoft.graph.models.ContentType;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.ItemActivity;
import com.microsoft.graph.models.List;
import com.microsoft.graph.models.ListItem;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.OnenotePage;
import com.microsoft.graph.models.Post;
import com.microsoft.graph.models.PrintTaskDefinition;
import com.microsoft.graph.models.PrinterShare;
import com.microsoft.graph.models.SectionGroup;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.models.StringKeyAttributeMappingSourceValuePair;
import com.microsoft.graph.models.TeamInfo;
import com.microsoft.graph.models.WorkbookChart;
import com.microsoft.graph.models.WorkbookNamedItem;
import com.microsoft.graph.models.WorkbookOperationError;
import com.microsoft.graph.models.WorkbookPivotTable;
import com.microsoft.graph.models.WorkbookTable;
import com.microsoft.graph.models.termstore.Group;
import com.microsoft.graph.models.termstore.Relation;
import com.microsoft.graph.models.termstore.Set;
import com.microsoft.graph.models.termstore.Term;

/**
 * A method filter to exclude internal and cyclic methods from the Azure Graph models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphConventionContext implements ConventionContext {

    public static final ConventionContext INSTANCE = new AzureGraphConventionContext();

    private AzureGraphConventionContext() {
    }

    @Override
    public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
        switch (method.getName()) {
            case "getFieldDeserializers":
            case "getBackingStore":
                return null;
            // There are cycles in the model
            case "getActivity":
                return method.getDeclaringClass() != ActivityHistoryItem.class ? this : NestedUserActivityConventionContext.INSTANCE;
            case "getCalendar":
                return method.getDeclaringClass() != Event.class ? this : NestedCalendarConventionContext.INSTANCE;
            case "getInstances":
                return method.getDeclaringClass() != Event.class ? this : NestedEventConventionContext.INSTANCE;
            case "getReplies":
                return method.getDeclaringClass() != ChatMessage.class ? this : NestedChatMessageConventionContext.INSTANCE;
            case "getValue":
                return method.getDeclaringClass() != StringKeyAttributeMappingSourceValuePair.class ? this : null;
            case "getChildFolders":
                if (method.getDeclaringClass() == ContactFolder.class) {
                    return NestedContactFolderConventionContext.INSTANCE;
                }
                if (method.getDeclaringClass() == MailFolder.class) {
                    return NestedMailFolderConventionContext.INSTANCE;
                }
                return this;
            case "getPrinter":
                return method.getDeclaringClass() != PrinterShare.class ? this : NestedPrinterConventionContext.INSTANCE;
            case "getTasks":
                return method.getDeclaringClass() != PrintTaskDefinition.class ? this : NestedPrintTaskConventionContext.INSTANCE;
            case "getParentNotebook":
                return method.getDeclaringClass() != SectionGroup.class ? this : NestedNotebookConventionContext.INSTANCE;
            case "getParentSectionGroup":
                return method.getDeclaringClass() != SectionGroup.class ? this : NestedSectionGroupConventionContext.INSTANCE;
            case "getParentSection":
                return method.getDeclaringClass() != OnenotePage.class ? this : NestedOnenoteSectionConventionContext.INSTANCE;
            case "getChildren":
                if (method.getDeclaringClass() == DriveItem.class) {
                    return NestedDriveItemConventionContext.INSTANCE;
                }
                if (method.getDeclaringClass() == Term.class || method.getDeclaringClass() == Set.class) {
                    return NestedTermConventionContext.INSTANCE;
                }
                return this;
            case "getTeam":
                return method.getDeclaringClass() != TeamInfo.class ? this : NestedTeamConventionContext.INSTANCE;
            case "getInReplyTo":
                return method.getDeclaringClass() != Post.class ? this : NestedPostConventionContext.INSTANCE;
            case "getSites":
                return method.getDeclaringClass() != Site.class ? this : NestedSiteConventionContext.INSTANCE;
            case "getSet":
                return method.getDeclaringClass() != Relation.class ? this : NestedSetConventionContext.INSTANCE;
            case "getSets":
                return method.getDeclaringClass() != Group.class ? this : NestedSetConventionContext.INSTANCE;
            case "getParentGroup":
                return method.getDeclaringClass() != Group.class ? this : NestedGroupConventionContext.INSTANCE;
            case "getDrive":
                return method.getDeclaringClass() != List.class ? this : NestedDriveConventionContext.INSTANCE;
            case "getDriveItem":
                return method.getDeclaringClass() != ItemActivity.class && method.getDeclaringClass() != ListItem.class ? this : NestedDriveItemConventionContext.INSTANCE;
            case "getWorksheet":
                return method.getDeclaringClass() != WorkbookChart.class
                        && method.getDeclaringClass() != WorkbookNamedItem.class
                        && method.getDeclaringClass() != WorkbookTable.class
                        && method.getDeclaringClass() != WorkbookPivotTable.class ? this : NestedWorksheetConventionContext.INSTANCE;
            case "getSourceColumn":
                return method.getDeclaringClass() != ColumnDefinition.class ? this : null;
            case "getInnerError":
                return method.getDeclaringClass() != WorkbookOperationError.class ? this : null;
            case "getBase":
            case "getBaseTypes":
                return method.getDeclaringClass() != ContentType.class ? this : NestedContentTypeConventionContext.INSTANCE;
            case "getTerms":
                return method.getDeclaringClass() != Set.class ? this : NestedTermConventionContext.INSTANCE;
            case "getFromTerm":
            case "getToTerm":
                return method.getDeclaringClass() != Relation.class ? this : NestedTermConventionContext.INSTANCE;
            case "getCreatedByUser":
            case "getLastModifiedByUser":
                return method.getDeclaringClass() != BaseItem.class ? this : NestedUserConventionContext.INSTANCE;
            default:
                return this;
        }
    }

    private static final class NestedUserConventionContext implements ConventionContext {

        private static final NestedUserConventionContext INSTANCE = new NestedUserConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedTermConventionContext implements ConventionContext {

        private static final NestedTermConventionContext INSTANCE = new NestedTermConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedDriveItemConventionContext implements ConventionContext {

        private static final NestedDriveItemConventionContext INSTANCE = new NestedDriveItemConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedContactFolderConventionContext implements ConventionContext {

        private static final NestedContactFolderConventionContext INSTANCE = new NestedContactFolderConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedUserActivityConventionContext implements ConventionContext {

        private static final NestedUserActivityConventionContext INSTANCE = new NestedUserActivityConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedCalendarConventionContext implements ConventionContext {

        private static final NestedCalendarConventionContext INSTANCE = new NestedCalendarConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedEventConventionContext implements ConventionContext {

        private static final NestedEventConventionContext INSTANCE = new NestedEventConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedChatMessageConventionContext implements ConventionContext {

        private static final NestedChatMessageConventionContext INSTANCE = new NestedChatMessageConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedWorksheetConventionContext implements ConventionContext {

        private static final NestedWorksheetConventionContext INSTANCE = new NestedWorksheetConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedSetConventionContext implements ConventionContext {

        private static final NestedSetConventionContext INSTANCE = new NestedSetConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedGroupConventionContext implements ConventionContext {

        private static final NestedGroupConventionContext INSTANCE = new NestedGroupConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedContentTypeConventionContext implements ConventionContext {

        private static final NestedContentTypeConventionContext INSTANCE = new NestedContentTypeConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedDriveConventionContext implements ConventionContext {

        private static final NestedDriveConventionContext INSTANCE = new NestedDriveConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedNotebookConventionContext implements ConventionContext {

        private static final NestedNotebookConventionContext INSTANCE = new NestedNotebookConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedSectionGroupConventionContext implements ConventionContext {

        private static final NestedSectionGroupConventionContext INSTANCE = new NestedSectionGroupConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedOnenoteSectionConventionContext implements ConventionContext {

        private static final NestedOnenoteSectionConventionContext INSTANCE = new NestedOnenoteSectionConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedSiteConventionContext implements ConventionContext {

        private static final NestedSiteConventionContext INSTANCE = new NestedSiteConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedTeamConventionContext implements ConventionContext {

        private static final NestedTeamConventionContext INSTANCE = new NestedTeamConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedPostConventionContext implements ConventionContext {

        private static final NestedPostConventionContext INSTANCE = new NestedPostConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedMailFolderConventionContext implements ConventionContext {

        private static final NestedMailFolderConventionContext INSTANCE = new NestedMailFolderConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedPrinterConventionContext implements ConventionContext {

        private static final NestedPrinterConventionContext INSTANCE = new NestedPrinterConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedPrintTaskConventionContext implements ConventionContext {

        private static final NestedPrintTaskConventionContext INSTANCE = new NestedPrintTaskConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

    private static final class NestedPublicIpAddressConventionContext implements ConventionContext {

        private static final NestedPublicIpAddressConventionContext INSTANCE = new NestedPublicIpAddressConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
                case "getFieldDeserializers":
                case "getBackingStore":
                    return null;
                // Filter out cycles in the model
                case "getId":
                    return this;
                default:
                    return null;
            }
        }
    }

}
