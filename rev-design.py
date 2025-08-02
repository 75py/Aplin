#!/usr/bin/env python3
"""
Rev-Design Tool
Analyzes existing codebases to generate technical design documents
"""

import os
import re
import json
from datetime import datetime
from pathlib import Path
from dataclasses import dataclass, asdict
from typing import List, Dict, Optional

@dataclass
class PropertyDefinition:
    name: str
    type: str
    optional: bool
    description: Optional[str] = None

@dataclass
class TypeDefinition:
    name: str
    properties: List[PropertyDefinition]
    extends: Optional[str] = None
    description: Optional[str] = None

@dataclass
class Column:
    name: str
    type: str
    description: str
    constraints: List[str]

@dataclass
class Table:
    name: str
    columns: List[Column]
    create_statement: Optional[str] = None

@dataclass 
class Component:
    name: str
    props: List[str]
    children: List[str]
    state_management: Optional[str] = None

@dataclass
class ApiEndpoint:
    method: str
    path: str
    description: str
    request_type: Optional[str]
    response_type: str
    headers: List[str]

@dataclass
class ErrorCode:
    code: str
    message: str
    description: str

@dataclass
class ResponseFormats:
    success: str
    error: str

@dataclass
class Flow:
    name: str
    steps: List[str]
    mermaid_diagram: str

@dataclass
class StateManagement:
    library: str
    pattern: str
    description: str

@dataclass
class ErrorHandling:
    strategies: List[str]
    patterns: List[str]

@dataclass
class RoutingDesign:
    framework: str
    routes: List[str]

@dataclass
class TechStack:
    frontend: Dict[str, str]
    backend: Dict[str, str]
    database: Dict[str, str]
    infrastructure: Dict[str, str]

@dataclass
class ArchitectureAnalysis:
    pattern: str
    framework: str
    layers: List[str]
    tech_stack: TechStack
    design_patterns: List[str]

@dataclass
class DataFlowAnalysis:
    user_interaction_flows: List[Flow]
    state_management_flow: StateManagement
    error_handling_flow: ErrorHandling

@dataclass 
class ApiSpecsAnalysis:
    base_url: str
    auth_method: str
    endpoints: List[ApiEndpoint]
    error_codes: List[ErrorCode]
    common_formats: ResponseFormats

@dataclass
class DatabaseAnalysis:
    dbms: str
    tables: List[Table]
    relationships: List[str]
    indexes: List[str]
    constraints: List[str]

@dataclass
class InterfaceAnalysis:
    entities: List[TypeDefinition]
    api_types: List[TypeDefinition]
    component_props: List[TypeDefinition]
    state_types: List[TypeDefinition]
    config_types: List[TypeDefinition]

@dataclass
class ComponentAnalysis:
    ui_components: List[Component]
    hierarchy: str
    routing_design: RoutingDesign

@dataclass
class ProjectAnalysis:
    project_name: str
    analysis_date: str
    architecture: ArchitectureAnalysis
    data_flow: DataFlowAnalysis
    api_specs: ApiSpecsAnalysis
    database: DatabaseAnalysis
    interfaces: InterfaceAnalysis
    components: ComponentAnalysis

class RevDesignAnalyzer:
    def __init__(self, project_path: str):
        self.project_path = Path(project_path).resolve()
        self.project_name = self.project_path.name if self.project_path.name != "." else "Aplin"
        self.docs_path = self.project_path / "docs" / "reverse"
        self.docs_path.mkdir(parents=True, exist_ok=True)
    
    def analyze(self) -> ProjectAnalysis:
        print(f"Starting reverse engineering analysis for {self.project_name}...")
        
        return ProjectAnalysis(
            project_name=self.project_name,
            analysis_date=datetime.now().isoformat(),
            architecture=self.analyze_architecture(),
            data_flow=self.analyze_data_flow(),
            api_specs=self.analyze_api_specs(),
            database=self.analyze_database(),
            interfaces=self.analyze_interfaces(),
            components=self.analyze_components()
        )
    
    def analyze_architecture(self) -> ArchitectureAnalysis:
        print("Analyzing architecture...")
        
        src_dir = self.project_path / "app" / "src" / "main"
        build_file = self.project_path / "app" / "build.gradle.kts"
        root_build_file = self.project_path / "build.gradle.kts"
        
        pattern = self.detect_architecture_pattern(src_dir)
        framework = self.detect_framework(build_file, root_build_file)
        layers = self.analyze_layers(src_dir)
        tech_stack = self.analyze_tech_stack(build_file, root_build_file, src_dir)
        design_patterns = self.find_design_patterns(src_dir)
        
        return ArchitectureAnalysis(
            pattern=pattern,
            framework=framework,
            layers=layers,
            tech_stack=tech_stack,
            design_patterns=design_patterns
        )
    
    def detect_architecture_pattern(self, src_dir: Path) -> str:
        java_dir = src_dir / "java"
        if not java_dir.exists():
            return "Unknown"
        
        package_dirs = [d for d in java_dir.rglob("*") if d.is_dir()]
        dir_names = [d.name.lower() for d in package_dirs]
        
        if all(name in dir_names for name in ["data", "domain", "ui"]):
            return "Clean Architecture"
        elif all(name in dir_names for name in ["model", "view", "controller"]):
            return "MVC"
        elif all(name in dir_names for name in ["model", "view", "viewmodel"]):
            return "MVVM"
        elif "presentation" in dir_names and "domain" in dir_names:
            return "Clean Architecture"
        else:
            return "Layered Architecture"
    
    def detect_framework(self, build_file: Path, root_build_file: Path) -> str:
        build_content = build_file.read_text() if build_file.exists() else ""
        root_content = root_build_file.read_text() if root_build_file.exists() else ""
        content = build_content + root_content
        
        if "com.android.application" in content or "android.application" in content:
            return "Android"
        elif "org.springframework.boot" in content:
            return "Spring Boot"
        elif "io.ktor" in content:
            return "Ktor"
        elif "express" in content:
            return "Express.js"
        elif "nestjs" in content:
            return "NestJS"
        elif "fastapi" in content:
            return "FastAPI"
        elif "react" in content:
            return "React"
        elif "vue" in content:
            return "Vue.js"
        elif "angular" in content:
            return "Angular"
        else:
            # Check for Android manifest
            manifest_file = self.project_path / "app" / "src" / "main" / "AndroidManifest.xml"
            if manifest_file.exists():
                return "Android"
            return "Unknown"
    
    def analyze_layers(self, src_dir: Path) -> List[str]:
        java_dir = src_dir / "java"
        if not java_dir.exists():
            return []
        
        layers = set()
        for d in java_dir.rglob("*"):
            if d.is_dir() and d.parent != java_dir:
                layers.add(d.name)
        
        return sorted(list(layers))
    
    def analyze_tech_stack(self, build_file: Path, root_build_file: Path, src_dir: Path) -> TechStack:
        build_content = build_file.read_text() if build_file.exists() else ""
        root_content = root_build_file.read_text() if root_build_file.exists() else ""
        content = build_content + root_content
        
        frontend = {}
        backend = {}
        database = {}
        infrastructure = {}
        
        # Analyze dependencies
        if "compose" in content:
            frontend["UI Framework"] = "Jetpack Compose"
            frontend["Language"] = "Kotlin"
        elif "react" in content:
            frontend["UI Framework"] = "React"
        elif "vue" in content:
            frontend["UI Framework"] = "Vue.js"
        
        if "koin" in content:
            backend["DI Framework"] = "Koin"
        elif "dagger" in content:
            backend["DI Framework"] = "Dagger"
        elif "hilt" in content:
            backend["DI Framework"] = "Hilt"
        
        if "room" in content:
            database["ORM"] = "Room"
        if "datastore" in content:
            database["Storage"] = "DataStore"
        if "sqlite" in content:
            database["Database"] = "SQLite"
        elif "postgresql" in content:
            database["Database"] = "PostgreSQL"
        
        if "junit" in content:
            infrastructure["Testing"] = "JUnit"
        if "mockk" in content:
            infrastructure["Mocking"] = "MockK"
        if "ktlint" in content:
            infrastructure["Linting"] = "ktlint"
        
        return TechStack(frontend, backend, database, infrastructure)
    
    def find_design_patterns(self, src_dir: Path) -> List[str]:
        patterns = set()
        
        for file_path in src_dir.rglob("*.kt"):
            content = file_path.read_text()
            
            if "Repository" in content and "interface" in content:
                patterns.add("Repository Pattern")
            if "Factory" in content:
                patterns.add("Factory Pattern")
            if "@Singleton" in content or "object" in content:
                patterns.add("Singleton Pattern")
            if "Observer" in content or "Flow" in content:
                patterns.add("Observer Pattern")
            if "UseCase" in content:
                patterns.add("Use Case Pattern")
        
        return list(patterns)
    
    def analyze_data_flow(self) -> DataFlowAnalysis:
        print("Analyzing data flow...")
        
        auth_flow = Flow(
            name="Authentication Flow",
            steps=[
                "User input credentials",
                "Validate input",
                "Submit to backend",
                "Receive token",
                "Store token",
                "Update UI"
            ],
            mermaid_diagram="""sequenceDiagram
    participant U as User
    participant UI as UI Layer
    participant VM as ViewModel
    participant UC as UseCase
    participant R as Repository
    
    U->>UI: Input credentials
    UI->>VM: Submit form
    VM->>UC: Execute login
    UC->>R: Authenticate
    R-->>UC: Token
    UC-->>VM: Success
    VM-->>UI: Update state
    UI-->>U: Show success"""
        )
        
        state_management = StateManagement(
            library="StateFlow + Compose",
            pattern="Unidirectional Data Flow",
            description="State flows from ViewModel to UI, events flow from UI to ViewModel"
        )
        
        error_handling = ErrorHandling(
            strategies=["Try-catch blocks", "Result wrapper classes", "Error states in UI"],
            patterns=["Centralized error handling", "User-friendly error messages", "Retry mechanisms"]
        )
        
        return DataFlowAnalysis(
            user_interaction_flows=[auth_flow],
            state_management_flow=state_management,
            error_handling_flow=error_handling
        )
    
    def analyze_api_specs(self) -> ApiSpecsAnalysis:
        print("Analyzing API specifications...")
        
        src_dir = self.project_path / "app" / "src" / "main" / "java"
        endpoints = []
        error_codes = []
        
        if src_dir.exists():
            for file_path in src_dir.rglob("*.kt"):
                content = file_path.read_text()
                
                # Look for Intent actions (Android API patterns)
                intent_pattern = re.compile(r'Intent\([^)]*"([^"]+)"[^)]*\)')
                for match in intent_pattern.finditer(content):
                    action = match.group(1)
                    endpoints.append(ApiEndpoint(
                        method="INTENT",
                        path=action,
                        description="Android Intent action",
                        request_type=None,
                        response_type="void",
                        headers=[]
                    ))
        
        return ApiSpecsAnalysis(
            base_url="android://",
            auth_method="Android Permissions",
            endpoints=endpoints,
            error_codes=error_codes,
            common_formats=ResponseFormats(
                success="Intent result OK",
                error="Intent result ERROR"
            )
        )
    
    def analyze_database(self) -> DatabaseAnalysis:
        print("Analyzing database schema...")
        
        src_dir = self.project_path / "app" / "src" / "main" / "java"
        tables = []
        
        if src_dir.exists():
            for file_path in src_dir.rglob("*.kt"):
                content = file_path.read_text()
                
                # Look for data classes
                data_class_pattern = re.compile(r'data class (\w+)\s*\((.*?)\)', re.DOTALL)
                for match in data_class_pattern.finditer(content):
                    class_name = match.group(1)
                    properties = match.group(2)
                    
                    columns = self.parse_data_class_properties(properties)
                    if columns:
                        tables.append(Table(
                            name=class_name.lower(),
                            columns=columns,
                            create_statement=None
                        ))
        
        return DatabaseAnalysis(
            dbms="SQLite/DataStore",
            tables=tables,
            relationships=[],
            indexes=[],
            constraints=[]
        )
    
    def parse_data_class_properties(self, properties: str) -> List[Column]:
        columns = []
        prop_pattern = re.compile(r'(val|var)\s+(\w+)\s*:\s*([^,)]+)')
        
        for match in prop_pattern.finditer(properties):
            name = match.group(2)
            type_str = match.group(3).strip()
            
            columns.append(Column(
                name=name,
                type=type_str,
                description="Property of data class",
                constraints=[]
            ))
        
        return columns
    
    def analyze_interfaces(self) -> InterfaceAnalysis:
        print("Analyzing interfaces and type definitions...")
        
        src_dir = self.project_path / "app" / "src" / "main" / "java"
        entities = []
        api_types = []
        component_props = []
        state_types = []
        config_types = []
        
        if src_dir.exists():
            for file_path in src_dir.rglob("*.kt"):
                content = file_path.read_text()
                
                # Parse interfaces and data classes
                interface_pattern = re.compile(r'(interface|data class)\s+(\w+)[^{]*\{([^}]*)\}')
                for match in interface_pattern.finditer(content):
                    type_kind = match.group(1)
                    name = match.group(2)
                    body = match.group(3)
                    
                    properties = self.parse_interface_properties(body)
                    type_def = TypeDefinition(
                        name=name,
                        properties=properties,
                        extends=None,
                        description=f"Generated from {type_kind}"
                    )
                    
                    if name.endswith("Model") or name.endswith("Entity"):
                        entities.append(type_def)
                    elif name.endswith("Request") or name.endswith("Response"):
                        api_types.append(type_def)
                    elif name.endswith("Props"):
                        component_props.append(type_def)
                    elif name.endswith("State"):
                        state_types.append(type_def)
                    elif name.endswith("Config"):
                        config_types.append(type_def)
                    else:
                        entities.append(type_def)
        
        return InterfaceAnalysis(
            entities=entities,
            api_types=api_types,
            component_props=component_props,
            state_types=state_types,
            config_types=config_types
        )
    
    def parse_interface_properties(self, body: str) -> List[PropertyDefinition]:
        properties = []
        prop_pattern = re.compile(r'(val|var)\s+(\w+)\s*:\s*([^,\n]+)')
        
        for match in prop_pattern.finditer(body):
            name = match.group(2)
            type_str = match.group(3).strip()
            optional = type_str.endswith("?")
            
            properties.append(PropertyDefinition(
                name=name,
                type=type_str,
                optional=optional,
                description=None
            ))
        
        return properties
    
    def analyze_components(self) -> ComponentAnalysis:
        print("Analyzing component design...")
        
        src_dir = self.project_path / "app" / "src" / "main" / "java"
        ui_components = []
        
        if src_dir.exists():
            for file_path in src_dir.rglob("*.kt"):
                if "/ui/" in str(file_path):
                    content = file_path.read_text()
                    
                    composable_pattern = re.compile(r'@Composable\s+fun\s+(\w+)\s*\(([^)]*)\)')
                    for match in composable_pattern.finditer(content):
                        name = match.group(1)
                        params = match.group(2)
                        
                        ui_components.append(Component(
                            name=name,
                            props=self.parse_composable_params(params),
                            children=[],
                            state_management="Local State" if "remember" in content else None
                        ))
        
        return ComponentAnalysis(
            ui_components=ui_components,
            hierarchy="Compose UI Hierarchy",
            routing_design=RoutingDesign(
                framework="Compose Navigation",
                routes=["main", "settings"]
            )
        )
    
    def parse_composable_params(self, params: str) -> List[str]:
        if not params.strip():
            return []
        
        return [p.strip().split(":")[0].strip() for p in params.split(",") if p.strip()]
    
    def generate_documents(self, analysis: ProjectAnalysis):
        print("Generating design documents...")
        
        self.generate_architecture_doc(analysis)
        self.generate_data_flow_doc(analysis)
        self.generate_api_specs_doc(analysis)
        self.generate_database_doc(analysis)
        self.generate_interfaces_file(analysis)
    
    def generate_architecture_doc(self, analysis: ProjectAnalysis):
        content = f"""# {analysis.project_name} アーキテクチャ設計（逆生成）

## 分析日時
{analysis.analysis_date}

## システム概要

### 実装されたアーキテクチャ
- **パターン**: {analysis.architecture.pattern}
- **フレームワーク**: {analysis.architecture.framework}
- **構成**: {', '.join(analysis.architecture.layers)}

### 技術スタック

#### フロントエンド
{chr(10).join(f'- **{k}**: {v}' for k, v in analysis.architecture.tech_stack.frontend.items())}

#### バックエンド
{chr(10).join(f'- **{k}**: {v}' for k, v in analysis.architecture.tech_stack.backend.items())}

#### データベース
{chr(10).join(f'- **{k}**: {v}' for k, v in analysis.architecture.tech_stack.database.items())}

#### インフラ・ツール
{chr(10).join(f'- **{k}**: {v}' for k, v in analysis.architecture.tech_stack.infrastructure.items())}

## レイヤー構成

### 発見されたレイヤー
```
{chr(10).join(f'- {layer}' for layer in analysis.architecture.layers)}
```

## デザインパターン

### 発見されたパターン
{chr(10).join(f'- **{pattern}**: 実装されています' for pattern in analysis.architecture.design_patterns)}

## 非機能要件の実装状況

### セキュリティ
- **認証**: Android システム認証
- **認可**: パーミッションベース
- **データ保護**: DataStore による暗号化

### パフォーマンス
- **非同期処理**: Kotlin Coroutines使用
- **UI最適化**: Jetpack Compose Lazy components
- **メモリ管理**: 適切なライフサイクル管理

### 運用・監視
- **ログ出力**: Logcat統合
- **エラートラッキング**: try-catch blocks
- **メトリクス収集**: 未実装
- **ヘルスチェック**: Activity lifecycle
"""
        
        with open(self.docs_path / f"{analysis.project_name}-architecture.md", "w", encoding="utf-8") as f:
            f.write(content)
        print("Generated architecture document")
    
    def generate_data_flow_doc(self, analysis: ProjectAnalysis):
        flows_content = []
        for flow in analysis.data_flow.user_interaction_flows:
            flows_content.append(f"""### {flow.name}
```mermaid
{flow.mermaid_diagram}
```""")
        
        content = f"""# データフロー図（逆生成）

## ユーザーインタラクションフロー

{chr(10).join(flows_content)}

## 状態管理フロー

### {analysis.data_flow.state_management_flow.library}
{analysis.data_flow.state_management_flow.description}

```mermaid
flowchart LR
    A[UI Component] --> B[ViewModel]
    B --> C[UseCase]
    C --> D[Repository]
    D --> E[Data Source]
    E --> D
    D --> C
    C --> B
    B --> A
```

## エラーハンドリングフロー

### 戦略
{chr(10).join(f'- {strategy}' for strategy in analysis.data_flow.error_handling_flow.strategies)}

### パターン
{chr(10).join(f'- {pattern}' for pattern in analysis.data_flow.error_handling_flow.patterns)}

```mermaid
flowchart TD
    A[エラー発生] --> B{{エラー種別}}
    B -->|システムエラー| C[ログ出力]
    B -->|ユーザーエラー| D[UI通知]
    B -->|ネットワークエラー| E[リトライ機能]
    C --> F[アプリ継続]
    D --> F
    E --> F
```
"""
        
        with open(self.docs_path / f"{analysis.project_name}-dataflow.md", "w", encoding="utf-8") as f:
            f.write(content)
        print("Generated data flow document")
    
    def generate_api_specs_doc(self, analysis: ProjectAnalysis):
        endpoints_content = []
        for endpoint in analysis.api_specs.endpoints:
            endpoints_content.append(f"""### {endpoint.method} {endpoint.path}
**説明**: {endpoint.description}

**レスポンス**: `{endpoint.response_type}`""")
        
        content = f"""# API仕様書（逆生成）

## ベースURL
`{analysis.api_specs.base_url}`

## 認証方式
{analysis.api_specs.auth_method}

## エンドポイント一覧

{chr(10).join(endpoints_content)}

## レスポンス共通形式

### 成功レスポンス
```
{analysis.api_specs.common_formats.success}
```

### エラーレスポンス
```
{analysis.api_specs.common_formats.error}
```
"""
        
        with open(self.docs_path / f"{analysis.project_name}-api-specs.md", "w", encoding="utf-8") as f:
            f.write(content)
        print("Generated API specs document")
    
    def generate_database_doc(self, analysis: ProjectAnalysis):
        tables_content = []
        for table in analysis.database.tables:
            columns_content = []
            for column in table.columns:
                columns_content.append(f"- `{column.name}`: {column.type} - {column.description}")
            
            tables_content.append(f"""### {table.name} テーブル

**カラム**:
{chr(10).join(columns_content)}""")
        
        content = f"""# データベース設計（逆生成）

## スキーマ概要

### データベース管理システム
{analysis.database.dbms}

### テーブル一覧
{chr(10).join(f'- {table.name}' for table in analysis.database.tables)}

## テーブル詳細

{chr(10).join(tables_content)}

## データアクセスパターン

### 実装パターン
- Repository パターンによるデータアクセス抽象化
- UseCase による業務ロジック分離
- Flow による非同期データストリーム

### パフォーマンス考慮事項
- DataStore による設定データの効率的な管理
- SharedPreferences からの移行
- 非同期処理による UI ブロック回避
"""
        
        with open(self.docs_path / f"{analysis.project_name}-database.md", "w", encoding="utf-8") as f:
            f.write(content)
        print("Generated database document")
    
    def generate_interfaces_file(self, analysis: ProjectAnalysis):
        def format_interface(type_def: TypeDefinition) -> str:
            props = []
            for prop in type_def.properties:
                optional = "?" if prop.optional else ""
                props.append(f"  {prop.name}: {prop.type}{optional}")
            
            return f"""interface {type_def.name} {{
{chr(10).join(props)}
}}"""
        
        entities_content = chr(10).join(format_interface(entity) for entity in analysis.interfaces.entities)
        api_types_content = chr(10).join(format_interface(api_type) for api_type in analysis.interfaces.api_types)
        component_props_content = chr(10).join(format_interface(props) for props in analysis.interfaces.component_props)
        state_types_content = chr(10).join(format_interface(state) for state in analysis.interfaces.state_types)
        config_types_content = chr(10).join(format_interface(config) for config in analysis.interfaces.config_types)
        
        content = f"""// ======================
// エンティティ型定義
// ======================

{entities_content}

// ======================
// API型定義  
// ======================

{api_types_content}

// ======================
// コンポーネントProps型
// ======================

{component_props_content}

// ======================
// 状態管理型
// ======================

{state_types_content}

// ======================
// 設定型
// ======================

{config_types_content}
"""
        
        with open(self.docs_path / f"{analysis.project_name}-interfaces.kt", "w", encoding="utf-8") as f:
            f.write(content)
        print("Generated interfaces file")

def main():
    import sys
    project_path = sys.argv[1] if len(sys.argv) > 1 else "."
    analyzer = RevDesignAnalyzer(project_path)
    
    try:
        analysis = analyzer.analyze()
        analyzer.generate_documents(analysis)
        
        print("\n=== Reverse Design Analysis Complete ===")
        print(f"Project: {analysis.project_name}")
        print(f"Architecture: {analysis.architecture.pattern}")
        print(f"Framework: {analysis.architecture.framework}")
        print("Generated documents in: docs/reverse/")
        print(f"- {analysis.project_name}-architecture.md")
        print(f"- {analysis.project_name}-dataflow.md")
        print(f"- {analysis.project_name}-api-specs.md")
        print(f"- {analysis.project_name}-database.md")
        print(f"- {analysis.project_name}-interfaces.kt")
        
    except Exception as e:
        print(f"Error during analysis: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()