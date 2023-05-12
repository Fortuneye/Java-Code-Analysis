package com.yourorganization.maven_sample;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Some code that uses JavaParser.
 */
public class CodeAnalysis {


    static Set<String> genericExceptions = new HashSet<>();
    static Set<String> jreExceptions = new HashSet<>();
    static Set<String> customExceptions = new HashSet<>();

    // Initialize counters for different exception types
    static AtomicInteger customExceptionsThrown = new AtomicInteger();
    static AtomicInteger jreExceptionsThrown = new AtomicInteger();
    static AtomicInteger genericExceptionsThrown = new AtomicInteger();

    static int numPublicPermissionField = 0;
    static int numProtectedPermissionField = 0;
    static int numPrivatePermissionField = 0;
    static int numDefaultPermissionField = 0;

    static int numPublicPermissionMethod = 0;
    static int numProtectedPermissionMethod = 0;
    static int numPrivatePermissionMethod = 0;
    static int numDefaultPermissionMethod = 0;

    /*
    * Helper code for methodLineCount
    * */
    private static int countLines(String code) {
        int numLines = 0;
        //string split the given segment of code
        String[] lines = code.split("\r\n|\r|\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                numLines++;
            }
        }
        return numLines;
    }

    private static boolean isJreException(ReferenceType exceptionType) {
        String packageName = exceptionType.getMetaModel().getPackageName();
        return packageName.startsWith("java.") || packageName.startsWith("javax.");
    }

    static int genericExceptionCaughtCount = 0;
    static int jreExceptionCaughtCount = 0;
    static int customExceptionCaughtCount = 0;


    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    /*public static void exceptionCaughtCount(File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                CompilationUnit cu = StaticJavaParser.parse(file);
                List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

                for (ClassOrInterfaceDeclaration classDecl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                    if (!classDecl.isInterface()) {
                        for (MethodDeclaration method : methods) {
                            boolean catchClausesPresent = method.getBody().orElseThrow().toTryStmt().isPresent();
                            if(catchClausesPresent){
                                NodeList<CatchClause> catchClauses = method.getBody().orElseThrow().toTryStmt().get().getCatchClauses();
                                for (CatchClause catchClause : catchClauses) {
                                    ReferenceType exceptionType = catchClause.getParameter().getType().asReferenceType();
                                    if (exceptionType.getArrayLevel() > 0) {
                                        exceptionType = exceptionType.getElementType().asReferenceType();
                                    }
                                    if (exceptionType.getElementType().isTypeParameter()) {
                                        genericExceptions.add(exceptionType.getElementType().asTypeParameter().getNameAsString());
                                    } else if (isJreException(exceptionType)) {
                                        jreExceptions.add(exceptionType.getElementType().toString());
                                    } else {
                                        customExceptions.add(exceptionType.getElementType().toString());
                                    }
                                }
                            }
                        }

                        genericExceptionCaughtCount += genericExceptions.size();
                        jreExceptionCaughtCount += jreExceptions.size();
                        customExceptionCaughtCount += customExceptions.size();


                        /*System.out.println("Number of methods with generic exceptions caught: " + genericExceptions.size());
                        System.out.println("Number of methods with JRE-defined exceptions caught: " + jreExceptions.size());
                        System.out.println("Number of methods with custom exceptions caught: " + customExceptions.size());*/
                    /*}
                }


        }).explore(pathDir);
    }*/

    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    public static void exceptionThrownCount (File pathDir) throws Exception {

        List<String> exceptionList = Arrays.asList(
                "AbstractMethodError",
                "AssertionError",
                "BootstrapMethodError",
                "ClassCastException",
                "ClassCircularityError",
                "ClassFormatError",
                "ClassNotFoundException",
                "CloneNotSupportedException",
                "EnumConstantNotPresentException",
                "ExceptionInInitializerError",
                "IllegalAccessError",
                "IllegalAccessException",
                "IllegalArgumentException",
                "IllegalMonitorStateException",
                "IllegalStateException",
                "IllegalThreadStateException",
                "IncompatibleClassChangeError",
                "IndexOutOfBoundsException",
                "InstantiationError",
                "InstantiationException",
                "InternalError",
                "InterruptedException",
                "LinkageError",
                "NegativeArraySizeException",
                "NoClassDefFoundError",
                "NoSuchFieldError",
                "NoSuchFieldException",
                "NoSuchMethodError",
                "NoSuchMethodException",
                "NullPointerException",
                "NumberFormatException",
                "OutOfMemoryError",
                "SecurityException",
                "StackOverflowError",
                "StringIndexOutOfBoundsException",
                "TypeNotPresentException",
                "UnsatisfiedLinkError",
                "UnsupportedClassVersionError",
                "UnsupportedOperationException",
                "VerifyError",
                "VirtualMachineError",
                "AnnotationFormatError",
                "AnnotationTypeMismatchException",
                "IncompleteAnnotationException",
                "IllegalClassFormatException",
                "UnmodifiableClassException",
                "LambdaConversionException",
                "WrongMethodTypeException",
                "ManagementException",
                "MemoryTypeNotSupportedException",
                "RuntimeErrorException",
                "RuntimeMBeanException",
                "RuntimeOperationsException",
                "FindException",
                "ResolutionException",
                "ResolvedModule",
                "GenericSignatureFormatError",
                "InaccessibleObjectException",
                "InvocationTargetException",
                "MalformedParameterizedTypeException",
                "MalformedParametersException",
                "UndeclaredThrowableException",
                "BindException",
                "ConnectException",
                "HttpRetryException",
                "HttpURLConnection",
                "MalformedURLException",
                "NoRouteToHostException",
                "PortUnreachableException",
                "ProtocolException",
                "Proxy",
                "ProxySelector",
                "ServerSocket",
                "Socket",
                "SocketException",
                "SocketTimeoutException",
                "URI",
                "URL",
                "URLConnection",
                "URLDecoder",
                "URLEncoder",
                "UnknownHostException",
                "UnknownServiceException",
                "BufferOverflowException",
                "BufferUnderflowException",
                "ByteBuffer",
                "CharBuffer",
                "DoubleBuffer",
                "FloatBuffer",
                "IntBuffer",
                "InvalidMarkException",
                "LongBuffer",
                "MappedByteBuffer",
                "ReadOnlyBufferException",
                "ShortBuffer",
                "CharacterCodingException",
                "IllegalCharsetNameException",
                "MalformedInputException",
                "StandardCharsets",
                "UnsupportedCharsetException",
                "AccessDeniedException",
                "AtomicMoveNotSupportedException",
                "ClosedDirectoryStreamException",
                "ClosedFileSystemException",
                "ClosedWatchServiceException",
                "DirectoryIteratorException",
                "DirectoryNotEmptyException",
                "FileAlreadyExistsException",
                "FileSystemException",
                "FileVisitResult",
                "Files",
                "InvalidPathException",
                "LinkOption",
                "NoSuchFileException",
                "NotDirectoryException",
                "NotLinkException",
                "OpenOption",
                "AnnotationFormatError",
                "AnnotationTypeMismatchException",
                "AWTError",
                "AWTException",
                "BackingStoreException",
                "BadAttributeValueExpException",
                "BadBinaryOpValueExpException",
                "BadLocationException",
                "BadStringOperationException",
                "BrokenBarrierException",
                "CertificateException",
                "CertificateEncodingException",
                "CertificateExpiredException",
                "CertificateNotYetValidException",
                "CharacterCodingException",
                "ClassNotFoundException",
                "CloneNotSupportedException",
                "ConcurrentModificationException",
                "DataFormatException",
                "DatatypeConfigurationException",
                "DateTimeException",
                "DestroyFailedException",
                "ExecutionException",
                "ExpandVetoException",
                "FontFormatException",
                "GeneralSecurityException",
                "GSSException",
                "IllegalClassFormatException",
                "IllegalAccessException",
                "IllegalArgumentException",
                "IllegalMonitorStateException",
                "IllegalPathStateException",
                "IllegalSelectorException",
                "IllegalStateException",
                "ImagingOpException",
                "IncompleteAnnotationException",
                "IndexOutOfBoundsException",
                "InheritableThreadLocal",
                "InstantiationException",
                "InterruptedException",
                "InvalidApplicationException",
                "InvalidKeyException",
                "InvalidKeySpecException",
                "InvalidMarkException",
                "InvalidObjectException",
                "InvalidParameterException",
                "InvalidPathException",
                "InvalidPreferencesFormatException",
                "InvalidPropertiesFormatException",
                "InvalidTargetObjectTypeException",
                "InvocationTargetException",
                "JarException",
                "JAXBException",
                "JMRuntimeException",
                "JMXProviderException",
                "JMXServerErrorException",
                "JMException",
                "KeyAlreadyExistsException",
                "KeyException",
                "KeyManagementException",
                "KeyNotFoundException",
                "LineUnavailableException",
                "LSException",
                "MarshalException",
                "MediaException",
                "MimeTypeParseException",
                "MissingResourceException",
                "MBeanException",
                "MBeanRegistrationException",
                "MBeanServerException",
                "MonitorSettingException",
                "NoSuchAlgorithmException",
                "NoSuchAttributeException",
                "NoSuchFieldException",
                "NoSuchMethodException",
                "NotActiveException",
                "NotBoundException",
                "NotFoundException",
                "NotOwnerException",
                "NotSerializableException",
                "NotYetBoundException",
                "NotYetConnectedException",
                "NoClassDefFoundError",
                "NullPointerException",
                "NumberFormatException",
                "ObjectStreamException",
                "IOException",
                "OptionalDataException",
                "OverlappingFileLockException",
                "ParserConfigurationException",
                "PatternSyntaxException",
                "PrinterException",
                "PrivilegedActionException",
                "ProcessException",
                "PropertyVetoException",
                "ProtocolException",
                "ProviderException",
                "RemoteException",
                "RuntimeException",
                "SAXException",
                "SAXNotSupportedException",
                "SAXParseException",
                "ScriptException",
                "SecurityException",
                "ServerNotActiveException",
                "SOAPException",
                "SQLException",
                "SSLException",
                "SyncFailedException",
                "SyntaxErrorException",
                "TimeoutException",
                "TooManyListenersException",
                "TransformerException",
                "TransformerFactoryConfigurationError",
                "TransformerFactoryConfigurationError",
                "UnmodifiableClassException",
                "UnsupportedOperationException",
                "UnsupportedTemporalTypeException",
                "UnsupportedFlavorException",
                "UriSyntaxException",
                "UserException",
                "ValidationException",
                "VerifyError",
                "WSDLException",
                "WrongAdapter",
                "WrongPolicy",
                "XMLParseException",
                "XMLSignatureException",
                "XMLStreamException",
                "XPathException",
                "FileNotFoundException",
                "ZipException"
        );

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                CompilationUnit cu = StaticJavaParser.parse(file);

            // for each method in the source file
            cu.findAll(MethodDeclaration.class).forEach(md -> {

                // count all exceptions declared in the method signature
                md.getThrownExceptions().forEach(te -> {
                    String exceptionName = te.toString();
                    if (exceptionName.startsWith("java.")) {
                        jreExceptionsThrown.getAndIncrement();
                    } else if (exceptionName.contains(".")) {
                        customExceptionsThrown.getAndIncrement();
                    }else if (exceptionList.contains(exceptionName)){
                        jreExceptionsThrown.getAndIncrement();
                    } else if (!exceptionName.equals("Exception") && !exceptionName.equals("Error")) {
                        customExceptionsThrown.getAndIncrement();
                    } else {
                        genericExceptionsThrown.getAndIncrement();
                    }
                });

                md.getBody().ifPresent(body -> body.findAll(ThrowStmt.class).forEach(stmt -> {
                    Expression expr = stmt.getExpression();
                    if (expr instanceof ObjectCreationExpr) {
                        //String exceptionName = ((ObjectCreationExpr) expr).getType().getNameAsString();
                        ObjectCreationExpr objExpr = (ObjectCreationExpr) expr;
                        ResolvedType exceptionType = null;
                        boolean escapeTryCatch = false;
                        try {
                            exceptionType = objExpr.getType().resolve();
                            escapeTryCatch = true;
                        } catch (UnsolvedSymbolException e) {
                            customExceptionsThrown.getAndIncrement();
                        }
                        if (escapeTryCatch) {
                            String fullyQualifiedName = exceptionType.asReferenceType().getQualifiedName();
                            if (fullyQualifiedName.startsWith("java.")) {
                                jreExceptionsThrown.getAndIncrement();
                            } else if (fullyQualifiedName.contains(".")) {
                                customExceptionsThrown.getAndIncrement();
                            }else if (exceptionList.contains(fullyQualifiedName)){
                                jreExceptionsThrown.getAndIncrement();
                            } else if (!fullyQualifiedName.equals("Exception") && !fullyQualifiedName.equals("Error")) {
                                customExceptionsThrown.getAndIncrement();
                            } else {
                                genericExceptionsThrown.getAndIncrement();
                            }
                        }
                    }else {
                        genericExceptionsThrown.getAndIncrement();
                    }
                }));

            });

                /*System.out.println("Custom exceptions thrown: " + customExceptionsThrown);
                System.out.println("JRE exceptions thrown: " + jreExceptionsThrown);
                System.out.println("Generic exceptions thrown: " + genericExceptionsThrown);*/

        }).explore(pathDir);
    }

    static int globalRecursiveCount = 0;
    static int globalNonRecursiveCount = 0;


    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    public static void methodCallCount (File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                CompilationUnit cu = StaticJavaParser.parse(file);
                // Visit each method in the CompilationUnit and count the number of method calls
                MethodCallVisitor visitor = new MethodCallVisitor();
                visitor.visit(cu, null);

                globalRecursiveCount += visitor.recursiveCount;
                globalNonRecursiveCount += visitor.nonRecursiveCount;

                // Print the results
                /*System.out.println("Non-recursive method calls: " + visitor.getNonRecursiveCount());
                System.out.println("Recursive method calls: " + visitor.getRecursiveCount());*/

        }).explore(pathDir);
    }

    static int inheritanceCounter = 0;
    static int classCounter = 0;

    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    public static void inheritanceCount (File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                // Parse the source code file
                CompilationUnit cu = StaticJavaParser.parse(file);

                // Count the number of classes that use inheritance
                int count = 0;
                List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
                for (ClassOrInterfaceDeclaration clazz : classes) {
                    if (clazz.getExtendedTypes().isNonEmpty()) {
                        count++;
                    }
                }
                classCounter += classes.size();
                inheritanceCounter += count;

                // Print the result
                //System.out.println("Number of classes that use inheritance: " + count);

        }).explore(pathDir);
    }

    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    public static void memberPermissionCount (File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);
            AtomicInteger numPublicField = new AtomicInteger();
            AtomicInteger numProtectedField = new AtomicInteger();
            AtomicInteger numPrivateField = new AtomicInteger();
            AtomicInteger numDefaultField = new AtomicInteger();

            AtomicInteger numPublicMethod = new AtomicInteger();
            AtomicInteger numProtectedMethod = new AtomicInteger();
            AtomicInteger numPrivateMethod = new AtomicInteger();
            AtomicInteger numDefaultMethod = new AtomicInteger();

                // Parse the source code file
                CompilationUnit cu = StaticJavaParser.parse(file);

                // Count the number of members with each permission

            //get number of permissions for FIELD DECLARATIONS
            //find every field declaration, depending on the modifier type, increment the counter for each one found
            cu.findAll(FieldDeclaration.class, fd -> fd.getModifiers().contains(Modifier.publicModifier()))
                    .forEach(fd -> numPublicField.getAndIncrement());
            cu.findAll(FieldDeclaration.class, fd -> fd.getModifiers().contains(Modifier.protectedModifier()))
                    .forEach(fd -> numProtectedField.getAndIncrement());
            cu.findAll(FieldDeclaration.class, fd -> fd.getModifiers().contains(Modifier.privateModifier()))
                    .forEach(fd -> numPrivateField.getAndIncrement());
            cu.findAll(FieldDeclaration.class, fd -> !fd.getModifiers().contains(Modifier.publicModifier())
                            && !fd.getModifiers().contains(Modifier.protectedModifier())
                            && !fd.getModifiers().contains(Modifier.privateModifier()))
                    .forEach(fd -> numDefaultField.getAndIncrement());

            //find every method declaration, depending on the modifier type, increment the counter for each one found
            cu.findAll(MethodDeclaration.class, md -> md.getModifiers().contains(Modifier.publicModifier())).forEach(md -> numPublicMethod.getAndIncrement());
            cu.findAll(MethodDeclaration.class, md -> md.getModifiers().contains(Modifier.protectedModifier())).forEach(md -> numProtectedMethod.getAndIncrement());
            cu.findAll(MethodDeclaration.class, md -> md.getModifiers().contains(Modifier.privateModifier())).forEach(md -> numPrivateMethod.getAndIncrement());
            cu.findAll(MethodDeclaration.class, md -> !md.getModifiers().contains(Modifier.publicModifier())
                            && !md.getModifiers().contains(Modifier.protectedModifier())
                            && !md.getModifiers().contains(Modifier.privateModifier()))
                    .forEach(md -> numDefaultMethod.getAndIncrement());

                numPublicPermissionField += numPublicField.get();
                numPrivatePermissionField += numPrivateField.get();
                numProtectedPermissionField += numProtectedField.get();
                numDefaultPermissionField += numDefaultField.get();

                numPublicPermissionMethod += numPublicMethod.get();
                numPrivatePermissionMethod += numPrivateMethod.get();
                numProtectedPermissionMethod += numProtectedMethod.get();
                numDefaultPermissionMethod += numDefaultMethod.get();

                // Print the results
                /*System.out.println("Number of public members: " + numPublicPermission);
                System.out.println("Number of protected members: " + numProtectedPermission);
                System.out.println("Number of private members: " + numPrivatePermission);
                System.out.println("Number of default members: " + numDefaultPermission);*/

        }).explore(pathDir);
    }

    static int numFieldsMember = 0;
    static int numMethodsMember = 0;

    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    public static void memberCount (File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                // Parse the source code file
                CompilationUnit cu = StaticJavaParser.parse(file);

                // Count the number of members in the class
                int numFields = cu.findAll(FieldDeclaration.class).size();
                int numMethods = cu.findAll(MethodDeclaration.class).size();

                numFieldsMember += numFields;
                numMethodsMember += numMethods;

                // Print the results
                /*System.out.println("Number of fields: " + numFieldsMember);
                System.out.println("Number of methods: " + numMethodsMember);
                System.out.println("Total number of members: " + (numFieldsMember + numMethodsMember));*/

        }).explore(pathDir);
    }

    // Count the number of lines in each method
    static int numAccessorsLines = 0;
    static int numMutatorsLines = 0;
    static int numConstructorsLines = 0;
    static int numStaticMethodsLines = 0;
    static int numInstanceMethodsLines = 0;

    static int numAccessors = 0;
    static int numMutators = 0;
    static int numConstructors = 0;
    static int numStaticMethods = 0;
    static int numInstanceMethods = 0;

    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    public static void methodLineCount (File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                CompilationUnit cu = StaticJavaParser.parse(file);

                for (MethodDeclaration md : cu.findAll(MethodDeclaration.class)) {

                    // Check if the method is an accessor or mutator method
                    String methodName = md.getNameAsString();

                    //if the name starts with get[Something], has no parameters, and isn't a void type, it's a getter/accessor
                    boolean isAccessor = Pattern.matches("^get[A-Z].*", methodName) && md.getParameters().isEmpty() && !md.getType().isVoidType();

                    //if the name starts with set[Something], has one parameter, and is a void type, its a setter/mutator
                    boolean isMutator = Pattern.matches("^set[A-Z].*", methodName) && md.getParameters().size() == 1 && md.getType().isVoidType();

                    int method = countLines(md.getBody().toString()) - 2;

                    if (isAccessor) {
                        numAccessors++;
                        numAccessorsLines += method;
                    } else if (isMutator) {
                        numMutators++;
                        numMutatorsLines += method;
                    } else if (md.isStatic()) {
                        numStaticMethods++;
                        numStaticMethodsLines += method;
                    } else {
                        numInstanceMethods++;
                        numInstanceMethodsLines += method;
                    }
                }

                // Count the number of lines in each constructor
                for (ConstructorDeclaration constructor : cu.findAll(ConstructorDeclaration.class)) {
                    numConstructors++;
                    numConstructorsLines += constructor.getEnd().get().line - constructor.getBegin().get().line + 1;
                }

                // Print the results
                /*System.out.println("Number of lines in accessors: " + numAccessors);
                System.out.println("Number of lines in mutators: " + numMutators);
                System.out.println("Number of lines in constructors: " + numConstructors);
                System.out.println("Number of lines in static methods: " + numStaticMethods);
                System.out.println("Number of lines in instance methods: " + numInstanceMethods);*/

        }).explore(pathDir);
    }

    static int methodParams = 0;
    static int paramsZero = 0;
    static int paramsOnetoThree = 0;
    static int paramsFourtoSix = 0;
    static int paramsSevenorMore = 0;

    static int methodsZero = 0;
    static int methodsOnetoThree = 0;
    static int methodsFourtoSix = 0;
    static int methodsSevenorMore = 0;



    /*
     * Raw counts and distributions of number of parameters a method requires
     * */
    public static void methodCount (File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                CompilationUnit cu = StaticJavaParser.parse(file);

                for (ClassOrInterfaceDeclaration classDecl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                    if (!classDecl.isInterface()) {
                        methodParams += classDecl.getMethods().size();
                        int numMethds = classDecl.getMethods().size();

                        //System.out.println("Number of methods: " + classDecl.getMethods().size());
                            if (classDecl.getMethods().isEmpty()) {
                                methodsZero++;
                            } else if ((classDecl.getMethods().size() >= 1) && (classDecl.getMethods().size() <= 3)) {
                                methodsOnetoThree++;
                            } else if (classDecl.getMethods().size() >= 4 && classDecl.getMethods().size() <= 6) {
                                methodsFourtoSix++;
                            } else if (classDecl.getMethods().size() >= 7) {
                                methodsSevenorMore++;
                            }

                    }
                }
        }).explore(pathDir);
    }

    static int globalParamCount = 0;

    /*
    * Raw counts and distributions of number of parameters a method requires
    * */
    public static void parameterCount (File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

            CompilationUnit cu = StaticJavaParser.parse(file);

            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                    globalParamCount += method.getParameters().size();

                    if(method.getParameters().isEmpty()){
                        paramsZero++;
                    }else if(method.getParameters().size() >= 1 && method.getParameters().size() <= 3){
                        paramsOnetoThree++;
                    }else if(method.getParameters().size() >= 4 & method.getParameters().size() <= 6){
                        paramsFourtoSix++;
                    }else if(method.getParameters().size() >= 7) {
                        paramsSevenorMore++;
                    }
                }

        }).explore(pathDir);
    }

    static int globalPrimitiveTypeCount = 0;
    static int globalCompositeTypeCount = 0;
    static int globalBooleanCount = 0;
    static int globalCharCount = 0;
    static int globalShortCount = 0;
    static int globalLongCount = 0;
    static int globalByteCount = 0;
    static int globalFloatCount = 0;
    static int globalDoubleCount = 0;
    static int globalIntCount = 0;


    /*
     * Frequency of data types used in parameters, local variables, and members of classes:
     * Raw counts and percentage of primitive vs composite types
     * Raw counts and percentage of each of the primitive types
     * */
    public static void variableTypeCount(File pathDir) throws Exception {

        /*
         * list of primitive types in java:
         * boolean
         * byte
         * short
         * char
         * int
         * long
         * float
         * double
         * */

        /*
         * we want raw number counts of composite types
         * (types containing primitive types, ie objects)
         * */

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);

                TypeSolver typeSolver = new CombinedTypeSolver();

                JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
                StaticJavaParser
                        .getConfiguration()
                        .setSymbolResolver(symbolSolver);

                CompilationUnit cu = StaticJavaParser.parse(file);

                VariableVisitor visitor = new VariableVisitor();
                cu.accept(visitor, null);

                globalPrimitiveTypeCount += visitor.primitiveTypeCount.get();
                globalCompositeTypeCount += visitor.compsositeTypeCount.get();
                globalBooleanCount += visitor.booleanCount.get();
                globalCharCount += visitor.charCount.get();
                globalShortCount += visitor.shortCount.get();
                globalLongCount += visitor.longCount.get();
                globalByteCount += visitor.byteCount.get();
                globalFloatCount += visitor.floatCount.get();
                globalDoubleCount += visitor.doubleCount.get();
                globalIntCount += visitor.intCount.get();

                /*System.out.println("Number of primitive types: " + visitor.primitiveTypeCount);
                System.out.println("Number of composite types: " + visitor.compsositeTypeCount);
                System.out.println("Number of boolean: " + visitor.booleanCount);
                System.out.println("Number of char: " + visitor.charCount);
                System.out.println("Number of short: " + visitor.shortCount);
                System.out.println("Number of long: " + visitor.longCount);
                System.out.println("Number of byte: " + visitor.byteCount);
                System.out.println("Number of float: " + visitor.floatCount);
                System.out.println("Number of double: " + visitor.doubleCount);
                System.out.println("Number of int: " + visitor.intCount);*/
        }).explore(pathDir);
    }

    public static String fileToString(File file) throws IOException, FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.lineSeparator());
        }
        reader.close();
        return stringBuilder.toString();
    }

    static int globallineCount = 0;

    static void javaLineCount(File pathDir) throws Exception {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println("\n" + path);
            // Specify the Java source code file to count lines

            /*String fullFile = fileToString(file);
            int lineCount = countLines(fullFile);
            globallineCount += lineCount;*/

            int numLines = 0;
            int lineCount = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.readLine() != null) {
                    lineCount++;
                    globallineCount++;
                }
            }

            // Parse the Java source code file using JavaParser
            /*CompilationUnit cu = StaticJavaParser.parse(file);

            // Get all the types (classes and interfaces) declared in the source code file
            cu.getTypes().forEach(type -> {
                // Get the name of the type
                String typeName = type.getName().asString();

                // Get the number of lines of the type
                int lineCount = type.getEnd().get().line - type.getBegin().get().line + 1;

                globallineCount += lineCount;
            });*/
            // Print the result
            //System.out.println(lineCount + " lines");
        }).explore(pathDir);
        }


    public static void main(String[] args) throws Exception {
        int caughtErrors = 0;
        //File newDir = new File("C:\\Users\\choco\\OneDrive - University at Albany - SUNY\\CLASSES\\SOPHOMORE\\SPRING 2021\\ICSI213 - Data St\\PROJECTS\\Project 4\\PROJ4");
        //5377
        for(int i = 0; i < 5377; i++) {
            String directory = "E:\\REPOS\\" + i + "\\";
            //File newDir = new File("C:\\Users\\choco\\OneDrive - University at Albany - SUNY\\CLASSES\\SOPHOMORE\\SPRING 2021\\ICSI213 - Data St\\PROJECTS\\Project 2\\PROJECT 2");
            File newDir = new File(directory);
            try {
                variableTypeCount(newDir);
                methodCount(newDir);
                parameterCount(newDir);
                methodLineCount(newDir);
                memberCount(newDir);
                memberPermissionCount(newDir);
                inheritanceCount(newDir);
                methodCallCount(newDir);
                exceptionThrownCount(newDir);
                //exceptionCaughtCount(newDir);
                javaLineCount(newDir);
                System.out.println(i);
            }catch(Exception e){
                System.out.println("CAUGHT");
                caughtErrors++;
                continue;
            }

        }

        /*variableTypeCount(newDir);
        methodCount(newDir);
        parameterCount(newDir);
        methodLineCount(newDir);
        memberCount(newDir);
        memberPermissionCount(newDir);
        inheritanceCount(newDir);
        methodCallCount(newDir);
        exceptionThrownCount(newDir);
        //exceptionCaughtCount(newDir);
        javaLineCount(newDir);*/

        System.out.println("CAUGHT ERRORS: " + caughtErrors);
        System.out.println("\nTOTALS\n");
        System.out.println("\nVARIABLE TOTALS: ");
        System.out.println("Number of primitive types: " + globalPrimitiveTypeCount);
        System.out.println("Number of composite types: " + globalCompositeTypeCount);
        System.out.println("Number of boolean: " + globalBooleanCount);
        System.out.println("Number of char: " + globalCharCount);
        System.out.println("Number of short: " + globalShortCount);
        System.out.println("Number of long: " + globalLongCount);
        System.out.println("Number of byte: " + globalByteCount);
        System.out.println("Number of float: " + globalFloatCount);
        System.out.println("Number of double: " + globalDoubleCount);
        System.out.println("Number of int: " + globalIntCount);

        System.out.println("\nPARAMETER NUMBER TOTALS:");
        System.out.println("Number of methods with 0 parameters: " + paramsZero);
        System.out.println("Number of methods with 1-3 parameters: " + paramsOnetoThree);
        System.out.println("Number of methods with 4-6 parameters: " + paramsFourtoSix);
        System.out.println("Number of methods with 7+ parameters: " + paramsSevenorMore);
        System.out.println("Number of total Parameters: " + globalParamCount);

        System.out.println("\nMETHOD NUMBER TOTALS:");
        System.out.println("Number of classes with 0 methods: " + methodsZero);
        System.out.println("Number of classes with 1-3 methods: " + methodsOnetoThree);
        System.out.println("Number of classes with 4-6 methods: " + methodsFourtoSix);
        System.out.println("Number of classes with 7+ methods: " + methodsSevenorMore);
        System.out.println("Number of total Methods: " + methodParams);

        System.out.println("\nNUMBER OF ACCESS/MUT/CONST/STATIC/INST METHODS");
        System.out.println("Number of accessors: " + numAccessors);
        System.out.println("Number of mutators: " + numMutators);
        System.out.println("Number of constructors: " + numConstructors);
        System.out.println("Number of static methods: " + numStaticMethods);
        System.out.println("Number of instance methods: " + numInstanceMethods);
        System.out.println("Number of lines in accessors: " + numAccessorsLines);
        System.out.println("Number of lines in mutators: " + numMutatorsLines);
        System.out.println("Number of lines in constructors: " + numConstructorsLines);
        System.out.println("Number of lines in static methods: " + numStaticMethodsLines);
        System.out.println("Number of lines in instance methods: " + numInstanceMethodsLines);

        System.out.println("\nMEMBER PERMISSION COUNT");
        System.out.println("Number of public fields: " + numPublicPermissionField);
        System.out.println("Number of protected fields: " + numProtectedPermissionField);
        System.out.println("Number of private fields: " + numPrivatePermissionField);
        System.out.println("Number of default fields: " + numDefaultPermissionField);
        System.out.println("Number of public methods: " + numPublicPermissionMethod);
        System.out.println("Number of protected methods: " + numProtectedPermissionMethod);
        System.out.println("Number of private methods: " + numPrivatePermissionMethod);
        System.out.println("Number of default methods: " + numDefaultPermissionMethod);

        System.out.println("\nNUMBER OF MEMBERS IN CLASS");
        System.out.println("Number of fields: " + numFieldsMember);
        System.out.println("Number of methods: " + numMethodsMember);
        System.out.println("Total number of members: " + (numFieldsMember + numMethodsMember));
        
        System.out.println("\nNUMBER OF INHERITED CLASSES");
        System.out.println("Number of classes: " + classCounter);
        System.out.println("Number of classes that use inheritance: " + inheritanceCounter);

        System.out.println("\nNUMBER OF RECURSIVE + NON-RECURSIVE CALLS");
        System.out.println("Number of recursive calls: " + globalRecursiveCount);
        System.out.println("Number of non-recursive calls: " + globalNonRecursiveCount);

        System.out.println("\nNUMBER OF EXCEPTIONS THROWN");
        System.out.println("Custom exceptions thrown: " + customExceptionsThrown);
        System.out.println("JRE exceptions thrown: " + jreExceptionsThrown);
        System.out.println("Generic exceptions thrown: " + genericExceptionsThrown);
        /*System.out.println("\nNUMBER OF EXCEPTIONS CAUGHT");
        System.out.println("Number of methods with generic exceptions caught: " + genericExceptionCaughtCount);
        System.out.println("Number of methods with JRE-defined exceptions caught: " + jreExceptionCaughtCount);
        System.out.println("Number of methods with custom exceptions caught: " + customExceptionCaughtCount);*/
        System.out.println("\nTOTAL LINE COUNT");
        System.out.println("Lines read: " + globallineCount);
    }
}
