import argparse
import json
import shutil
import subprocess
import re
from functools import reduce
from enum import Enum, auto
from os import listdir, makedirs, remove, getcwd, chdir, environ
from os.path import isdir, join, exists
from datetime import date
import dateutil.parser

# Color
CEND    = '\33[0m'
CRED    = '\33[31m'
CGREEN  = '\33[32m'
CYELLOW = '\33[33m'
def print_red(msg):
    print(f"{CRED}{msg}{CEND}")
    return msg
def print_green(msg):
    print(f"{CGREEN}{msg}{CEND}")
    return msg
def print_yellow(msg):
    print(f"{CYELLOW}{msg}{CEND}")
    return msg

# Path
JSTAR_HOME = environ["JSTAR_HOME"]
EVAL_HOME = join(JSTAR_HOME, "eval")
LOG_DIR = join(JSTAR_HOME, "logs", "analyze")
ECMA_DIR = join(JSTAR_HOME, "ecma262")
RESULT_DIR = join(EVAL_HOME, "result")
RAW_DIR = join(RESULT_DIR, "raw")
DIFF_DIR = join(RESULT_DIR, "diff")
EVAL_LOG = join(RESULT_DIR, "log")

# Global
FIRST_VERSION = "fc85c50181b2b8d7d75f034800528d87fda6b654"
# ES2018_VERSION = "59d73dc08ea371866c1d9d45843e6752f26a48e4"
ES2018_VERSION = "8fadde42cf6a9879b4ab0cb6142b31c4ee501667"
COMMIT_REGEX = re.compile("^[a-z0-9]{40}$")
NO_REFINE = False
COMMIT_INFOS = {"unknown": None}

# Shell util
EVAL_LOG_POST = f"2>> {EVAL_LOG} 1>> {EVAL_LOG}"
def execute_sh(cmd, post = ""):
    proc = subprocess.Popen(f"{cmd} {post}", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = proc.communicate()
    proc.wait()
    return out.decode("utf-8"), err.decode("utf-8")
def get_head_commit():
    cmd = f"cd {ECMA_DIR}; git rev-parse HEAD"
    out, err = execute_sh(cmd)
    return out.strip() if err == '' else None
def get_prev_commit(commit_hash):
    cmd = f"cd {ECMA_DIR}; git rev-parse {commit_hash}^1"
    out, err = execute_sh(cmd)
    return out.strip() if err == '' else None
def get_all_commits():
    cmd = f"cd {ECMA_DIR}; git rev-list {FIRST_VERSION}"
    out, err = execute_sh(cmd)
    all_commits = out.split()
    return all_commits[:all_commits.index(ES2018_VERSION) + 1]
def get_commit_info(commit_hash):
    if COMMIT_REGEX.match(commit_hash) == None:
        return
    # author name, author email, commit date
    # https://m.blog.naver.com/dudwo567890/221481850543
    cmd = f"cd {ECMA_DIR}; git show -s --format=%an,%ae,%cI {commit_hash}"
    out, err = execute_sh(cmd)
    an, ae, cdate = out.strip().split(",")
    return {
        "version": commit_hash,
        "date": cdate,
        "author": an
    }
def get_remote_errors(remote_path, suffix):
    print(f"rsync {remote_path}...")
    result_dir = join(EVAL_HOME, "result" + suffix)
    cmd = f"rsync -a -m --include '**/errors' --include '**/stat_summary' --include='*/' --exclude='*' {remote_path}/raw {result_dir}"
    out, err = execute_sh(cmd)
    if err == "":
        print(f"rsync completed.")
    else:
        print(err)
def read_remote_file(host, path):
    cmd = f"ssh {host} 'cat {path}'"
    out, err = execute_sh(cmd)
    return out.strip(), err != ""
def get_commit_desc(commit_hash):
    cinfo = get_commit_info(commit_hash)
    return cinfo["date"] + "/" + commit_hash
def clean_dir(path):
    if exists(path):
        shutil.rmtree(path)
    makedirs(path)
def get_days_from_es2018(commit):
    finfo, cinfo = COMMIT_INFOS[ES2018_VERSION], COMMIT_INFOS[commit]
    fdate = dateutil.parser.isoparse(finfo["date"])
    cdate = dateutil.parser.isoparse(cinfo["date"])
    return (cdate - fdate).days

# Util
def build_jstar():
    if exists(EVAL_LOG):
        remove(EVAL_LOG)
    chdir(JSTAR_HOME)
    print("update...")
    execute_sh("git pull", EVAL_LOG_POST)
    execute_sh("git submodule update", EVAL_LOG_POST)
    print("build project...")
    execute_sh("sbt assembly", EVAL_LOG_POST)
    chdir(EVAL_HOME)
def run_analyze(version):
    desc = get_commit_desc(version)
    refine_opt = "-analyze:no-refine" if NO_REFINE else ""
    print(f"run analyze({desc}) {refine_opt}...")
    cmd = f"jstar analyze -time -log -silent -extract:version={version} {refine_opt}"
    execute_sh(cmd, EVAL_LOG_POST)
    execute_sh(f"mkdir -p {RAW_DIR}")
    version_dir = get_version_dir(version)
    execute_sh(f"rm -rf {version_dir}")
    execute_sh(f"cp -r {LOG_DIR} {version_dir}")
    print("completed...")
def get_target_errors():
    with open(join(EVAL_HOME, "errors.json"), "r") as f:
        return json.load(f)
def get_version_dir(version):
    return join(RAW_DIR, version)
def get_versions():
    return [v for v in listdir(RAW_DIR) if isdir(join(RAW_DIR, v))]
def has_result(version):
    return exists(get_version_dir(version))
def get_results(versions):
    return [AnalysisResult(v) for v in versions]
def log(f, print_func, msg):
    print_func(msg)
    f.write(msg + "\n")
def map2(map_func, a, b):
    return map_func(a), map_func(b)

# check type enumeration
class CheckErrorType(Enum):
    SOFT = auto()
    ON_DEMAND = auto()
    FORCE = auto()

# analysis result class
class AnalysisResult:
    # init
    def __init__(self, version):
        self.version = version
        with open(join(get_version_dir(version), "errors"), "r") as f:
            self.errors = set(f.read().splitlines())
        with open(join(get_version_dir(version), "stat_summary"), "r") as f:
            self.stats = list(f.read().split())
    # check if this analysis result contains `error`
    def contains(self, error):
        return error in self.errors
    # get diff with other analysis result
    def diff(self, that):
        return {
            "-": self.errors - that.errors,
            "+": that.errors - self.errors
        }
    # check bugs
    def check(self, bugs, f):
        for bug in bugs:
            if self.contains(bug):
                log(f, print_green, f"[PASS] @ {self.version}: {bug}")
            else:
                log(f, print_red, f"[FAIL] @ {self.version}: {bug}")
    # equality
    def __eq__(self, that):
        return isinstance(that, AnalysisResult) and self.errors == that.errors

# dump diffs of analysis results
def dump_diffs():
    print(f"remove diff directory: {DIFF_DIR}")
    clean_dir(DIFF_DIR)
    # calc diff of each versions and dump
    versions = get_all_commits()
    analyzed_versions = get_versions()
    for i, version in enumerate(versions):
        if not version in analyzed_versions:
            continue
        with open(join(DIFF_DIR, version), "w") as f:
            prev_version = get_prev_commit(versions[-1]) if version == versions[-1] else versions[i+1]
            f.write("================================================================================\n")
            f.write(f"Version          : {version}\n")
            f.write(f"Previous Version : {prev_version}\n")
            f.write("--------------------------------------------------------------------------------\n")
            # if previous version result doesn't exist, then
            if not prev_version in analyzed_versions:
                f.write(f"No analysis result for previous version")
                continue
            res = AnalysisResult(version)
            prev_res = AnalysisResult(prev_version)
            # if analysis result same
            if prev_res == res:
                f.write(f"Same analysis result with previous version")
            # otherwise print diff
            else:
                diff = prev_res.diff(res)
                for new_bug in sorted(diff["+"]):
                    f.write(f"+{new_bug}\n")
                for old_bug in sorted(diff["-"]):
                    f.write(f"-{old_bug}\n")

# dump bug diffs
def dump_bug_diffs():
    versions = get_versions()
    if len(versions) == 0:
        return 0
    errors_map = dict([(v, AnalysisResult(v).errors) for v in versions])
    errors = reduce(lambda acc, v: acc.union(errors_map[v]), errors_map, set())
    # sort version in ASC
    sorted_versions = [v for v in reversed(get_all_commits()) if v in versions]
    first_version = sorted_versions[0]
    results = dict([(e, []) for e in errors])
    # get diffs of each error
    for error in errors:
        found, created_at = False, f"unknown"
        for version in sorted_versions:
            contained = error in errors_map[version]
            if contained and not found:
                if version != first_version:
                    created_at = version
                found = True
            elif not contained and found:
                results[error].append((created_at, version))
                found = False
        # handle last version
        if found:
            results[error].append((created_at, "unknown"))
    # make pretty results
    pretty_results = []
    for e in results:
        infos, ttl = [], 0
        for created_at, deleted_at in results[e]:
            # get commit info of created, deleted
            cinfo, dinfo = COMMIT_INFOS[created_at], COMMIT_INFOS[deleted_at]
            # calc ttl
            parse_date = lambda i: None if not i else dateutil.parser.isoparse(i["date"])
            cdate, ddate = map(parse_date, [cinfo, dinfo])
            if not cdate or not ddate:
                local_ttl, ttl = "-", "-"
            else:
                local_ttl = (ddate - cdate).days
            if ttl != "-":
                ttl += local_ttl
            # add info
            infos.append({
                "created_info": cinfo,
                "deleted_info": dinfo,
                "TTL": str(local_ttl)
            })
        pretty_results.append({
            "errors": e,
            "infos": infos,
            "TTL": str(ttl)
        })
    # dump bug-diffs.json
    with open(join(RESULT_DIR, "bug-diffs.json"), "w") as f:
        json.dump(pretty_results, f, indent=2)
    # dump bug-diffs-summary
    true_bugs = reduce(lambda acc, e: acc.union(set(e["bugs"])), get_target_errors(), set())
    p1, tp1 = 0, 0
    with open(join(RESULT_DIR, "bug-diffs-summary.tsv"), "w") as f:
        writeln = lambda cells: f.write("\t".join(cells) + "\n")
        writeln(["bug",
            "c_author", "c_commit", "c_date",
            "r_author", "r_commit", "r_date",
            "TTL", "category", "kind", "T/F"])

        def get_cat_and_kind(bug):
            if "unknown variable" in bug :
                return ["Reference", "UnknownVar"]
            elif "already defined variable" in bug:
                return ["Reference", "DuplicatedVar"]
            elif "assertion failed" in bug:
                return ["Assertion", "Assertion"]
            elif "unchecked abrupt completion" in bug:
                return ["Type", "Abrupt"]
            elif "non-numeric types" in bug:
                return ["Type", "WrongNum"]
            elif "non-number types" in bug:
                return ["Type", "WrongNum"]
            elif "remaining parameter" in bug:
                return ["Arity", "MissingParam"]
            else:
                print(bug)
                raise NotImplementedError

        def get_info_data(info):
            if info == None:
                return ["-"] * 3
            version = info["version"]
            return [info["author"], version, str(get_days_from_es2018(version))]

        for pres in pretty_results:
            bug, bug_count = pres["errors"], len(pres["infos"])
            tf_str = "T" if bug in true_bugs else "F"
            for info in pres["infos"]:
                cinfo, dinfo = info["created_info"], info["deleted_info"]
                writeln([bug] +
                        get_info_data(cinfo) +
                        get_info_data(dinfo) +
                        [info["TTL"]] +
                        get_cat_and_kind(bug) +
                        [tf_str])
            p1 += bug_count
            tp1 += bug_count if bug in true_bugs else 0
    # return data for precision
    # assume that true_bugs are all detected
    # p0, tp0, p1, tp1
    return len(errors), len(true_bugs), p1, tp1

# dump diff summary
def dump_diff_summary():
    versions = get_versions()
    if len(versions) == 0:
        return
    results_map = dict([(v, AnalysisResult(v)) for v in versions])
    # sort version in DESC
    sorted_versions = [v for v in reversed(get_all_commits()) if v in versions]
    first_version = sorted_versions[0]
    with open(join(RESULT_DIR, "diff-summary.tsv"), "w") as f:
        writeln = lambda cells: f.write("\t".join(map(str, cells)) + "\n")
        size = lambda s: str(len(s))
        # columns: version | + | - | # of errors | date | # iter |
        #           parse | cfg | checker | analysis | fullFunc | allFunc
        #           node | return | all
        writeln(["version", "+", "-", "# of errors", "days", "# iter", "parse", "cfg", "checker", "analysis",
            "full", "all", "node", "return", "all"])
        for i in range(len(sorted_versions)):
            version = sorted_versions[i]
            result = results_map[version]
            commons = [size(result.errors), get_days_from_es2018(version)] + result.stats
            if version == first_version:
                writeln([first_version, "-", "-"] + commons)
            else:
                prev_result = results_map[sorted_versions[i-1]]
                diff = prev_result.diff(result)
                writeln([version, size(diff["+"]), size(diff["-"])] + commons)

def dump_sparse_targets():
    versions = get_all_commits()
    with open(join(EVAL_HOME, "sparse_targets"), "w") as f:
        for i, version in enumerate(versions):
            if version == ES2018_VERSION:
                f.write(version + "\n")
                break
            prev_version = versions[i+1]
            # if no analysis result, add to targets
            if not has_result(version) or not has_result(prev_version):
                f.write(version + "\n")
            else:
                prev_result, result = get_results([prev_version, version])
                # if diff is not empty, add to targets
                if prev_result != result:
                    f.write(version + "\n")

# dump stats
def dump_stat(stat_f):
    def print_header(msg):
        log(stat_f, print, "-" * 80)
        log(stat_f, print, msg)
        log(stat_f, print, "-" * 80)
    print_header("CHECK ERRORS")
    # check if target errors exist
    check_errors(CheckErrorType.SOFT, stat_f)

    # dump diffs of analysis results
    print_header("DUMP DIFFS")
    log(stat_f, print, f"calc diff for current results...")
    dump_diffs()
    p0, tp0, p1, tp1 = dump_bug_diffs()
    dump_diff_summary()
    log(stat_f, print, f"calc diff completed.")

    # dump sparse targets
    print_header("SPARSE TARGETS")
    log(stat_f, print, f"calc sparse targets...")
    dump_sparse_targets()
    log(stat_f, print, f"calc sparse completed.")

    # print precision
    print_header("SUMMARY")
    def dump_precision(n, p, tp):
        precision = tp / p
        precision_msg = "precision{}: {}/{}({:.4}%)".format(n, tp, p, precision * 100)
        log(stat_f, print, precision_msg)
    dump_precision(0, p0, tp0)
    dump_precision(1, p1, tp1)

# run analysis and check if target errors exist
def check_errors(option, check_f):
    print(f"check errors(option: {option})...")
    # get target errors
    for target_error in get_target_errors():
        version = target_error["version"]
        bugs = target_error["bugs"]
        # if analysis result for version not exist,
        if not exists(get_version_dir(version)):
            # if SOFT mode, dump YET and continue
            if option == CheckErrorType.SOFT:
                for bug in bugs:
                    log(check_f, print_yellow, f"[YET] @ {version}: {bug}")
            else:
                # otherwise, run analysis
                run_analyze(version)
        # if force mode run analysis
        elif option == CheckErrorType.FORCE:
            run_analyze(version)
        # check results and dump
        AnalysisResult(version).check(bugs, check_f)
    print("check errors completed.")

# strictly check target errors
def strict_check_errors(strict_f):
    versions = get_all_commits()
    print(f"strict-check errors...")
    # get target errors
    for target_error in get_target_errors():
        version = target_error["version"]
        bugs = target_error["bugs"]
        # handle first version
        if version == versions[0] and has_result(version):
            AnalysisResult(version).check(bugs, strict_f)
            continue
        next_version = versions[versions.index(version)-1]
        # log YET if analysis result is not found
        if not has_result(version) or not has_result(next_version):
            for bug in bugs:
                log(strict_f, print_yellow, f"[YET] @ {version}: {bug}")
        # strict check
        else:
            result, next_result = get_results([version, next_version])
            for bug in bugs:
                if result.contains(bug) and not next_result.contains(bug):
                    log(strict_f, print_green, f"[PASS] @ {version}: {bug}")
                else:
                    log(strict_f, print_red, f"[FAIL] @ {version}: {bug}")
    print(f"strict-check completed.")

# sparsely run analyzer based on previous analysis result
def sparse_run(sparse_targets, log_f):
    print(f"run-sparse started...")
    versions = get_all_commits()
    # calc sparse targets
    targets = [False] * len(versions)
    for v in versions:
        targets = v in sparse_targets
    # always analyze recent, es2018 commit
    targets[0], targets[-1] = True, True
    # run analyzer sparsely
    i, analyzed = 0, set()
    def analyze_once(v):
        if not v in analyzed:
            run_analyze(v)
            analyzed.add(v)
            log_f.write(v + "\n")
    def analyze_range(i0, i1):
        for v in versions[i0:i1]:
            analyze_once(v)
    while True:
        if versions[i] == ES2018_VERSION:
            analyze_once(version)
            break
        version = versions[i]
        nt_i = targets.index(True, i+1)
        # get nt_version and prev_version
        nt_version, prev_version = versions[nt_i], versions[i+1]
        # analyze 3 versions
        analyze_once(version)
        analyze_once(prev_version)
        analyze_once(nt_version)
        if nt_i - i <= 2:
            i = nt_i
            continue
        # compare nt_result and prev_result diff with previous results
        nt_result, prev_result = get_results([nt_version, prev_version])
        # if diff is still not found
        if nt_result == prev_result:
            for v in versions[i+2:nt_i]:
                desc = get_commit_desc(v)
                print(f"Skip {desc}...")
        else:
            analyze_range(i+2, nt_i)
        # move to next target
        i = nt_i
    print(f"run-sparse completed.")

# grep results from remote
def grep_remotes(addrs, suffix = ""):
    # read now, prev from addrs
    from_now, from_prev = True, True
    now_paths, prev_paths, hosts = [], [], []
    print(addrs)
    for addr in addrs:
        host = addr.split(":")[0]
        hosts.append(host)
        now_path, now_err = read_remote_file(host, "~/now" + suffix)
        prev_path, prev_err = read_remote_file(host, "~/prev" + suffix)
        if now_err:
            from_now = False
        else:
            now_paths.append(now_path)
        if prev_err:
            from_prev = False
        else:
            prev_paths.append(prev_path)
    # rsync with remotes
    def rsync_remotes(remote_paths):
        for i, remote_path in enumerate(remote_paths):
            get_remote_errors(hosts[i] + ":" + remote_path, suffix)
    if from_now:
        print("grep results from NOW" + suffix)
        rsync_remotes(now_paths)
    elif from_prev:
        print("grep results from PREV" + suffix)
        rsync_remotes(prev_paths)

# entry
def main():
    # parse arguments
    parser = argparse.ArgumentParser(description="evaluate analyzer result (run all versions if there are no options)")
    parser.add_argument( "--clean", action="store_true", default=False, help="clean result/* and run analysis to all versions" )
    parser.add_argument( "-s", "--stat", action="store_true", default=False, help="dump status of result/raw/*" )
    parser.add_argument( "-v", "--version", help="run analyzer to target version")
    parser.add_argument( "-c", "--check", action="store_true", default=False, help="check errors.json based on CACHED result/raw/*" )
    parser.add_argument( "-sc", "--scheck", action="store_true", default=False, help="strictly check errors.json based on result/raw/*" )
    parser.add_argument( "-fc", "--fcheck", action="store_true", default=False, help="check errors.json based on NEW result/raw/*" )
    parser.add_argument( "-nr", "--no-refine", action="store_true", default=False, help="use -analyze:no-refine during analysis" )
    parser.add_argument( "--stride", help="run analyzer based on stride(OFFSET/STRIDE)")
    parser.add_argument( "--sparse", help="run analyzer sparsely based on diff" )
    parser.add_argument( "-g", "--grep", type=lambda items:[item for item in items.split(",")], help="grep $JSTAR_HOME/eval/result/raw/*/error from remote")
    args = parser.parse_args()

    # make directory
    if args.clean or args.grep or not exists(RESULT_DIR):
        clean_dir(RESULT_DIR)
    if not exists(RAW_DIR):
        makedirs(RAW_DIR)
    if not exists(DIFF_DIR):
        makedirs(DIFF_DIR)

    # initialize
    print("initialization...")
    global COMMIT_INFOS
    for commit in get_all_commits():
        COMMIT_INFOS[commit] = get_commit_info(commit)

    # no-refine opt
    global NO_REFINE
    NO_REFINE = args.no_refine

    # build JSTAR
    if not args.stat and not args.grep:
        build_jstar()

    # command stat
    if args.stat:
        with open(join(RESULT_DIR, "stat.log"), "w") as f:
            dump_stat(f)
    # command check
    elif args.check:
        with open(join(RESULT_DIR, "check-errors.log"), "w") as f:
            check_errors(CheckErrorType.ON_DEMAND, f)
    # command force-check
    elif args.fcheck:
        with open(join(RESULT_DIR, "check-errors.log"), "w") as f:
            check_errors(CheckErrorType.FORCE, f)
    # command strict-check
    elif args.scheck:
        with open(join(RESULT_DIR, "scheck-errors.log"), "w") as f:
            strict_check_errors(f)
    # command run
    elif args.version != None:
        run_analyze(args.version)
    # command grep
    elif args.grep != None:
        grep_remotes(args.grep)
        grep_remotes(args.grep, "_np")
    # command sparse
    elif args.sparse != None:
        with open(join(RESULT_DIR, "analyzed"), "w") as log_f:
            with open(args.sparse, "r") as target_f:
                sparse_targets = set(target_f.read().strip().splitlines())
                sparse_run(sparse_targets, log_f)
    # command all
    else:
        # run all versions
        versions = get_all_commits()
        if args.stride != None:
            offset, stride = [int(n) for n in args.stride.split("/")]
            versions = versions[offset::stride]
        with open(join(RESULT_DIR, "analyzed"), "w") as f:
            for version in versions:
                run_analyze(version)
                f.write(version + "\n")

# run main
main()
